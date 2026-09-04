package io.petaleconomy.block.entities;

import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.gui.APDMainMenu;
import io.petaleconomy.gui.AbstractPetalMenu;
import io.petaleconomy.gui.CreatePetalAccountMenu;
import io.petaleconomy.item.PetalBill;
import io.petaleconomy.util.Constants;
import io.petaleconomy.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutomaticPetalDispenserBlockEntity extends BlockEntity implements MenuProvider {
    private AbstractPetalMenu menu;
    private final ItemStackHandler itemHandler = new ItemStackHandler(11){
        @Override
        protected void onContentsChanged(int slot) {

            setChanged();

            if (slot == Constants.CARD_SLOT && menu != null) {
                menu.updateCurrentAccount();
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == Constants.INPUT_SLOT && menu instanceof CreatePetalAccountMenu) { //update when deposit screen is made
                return isPetalBill(stack);
            }

            if (slot == Constants.CARD_SLOT) {
                return isPetalCard(stack);
            }

            if (slot >= Constants.ONE_BILL_SLOT && slot <= Constants.TEN_THOUSAND_BILL_SLOT) {
                return false;
            }

            return false;
        }
    };

    public boolean isPrimaryAccount;

    private static boolean isPetalBill(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_BILLS);
    }

    private static boolean isPetalCard(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_CARDS);
    }

    private static final int[] DENOMINATIONS = {
            1, 5, 10, 20, 50, 100, 500, 1000, 10000
    };

    //Deposit Menu/Screen
    private static final int INPUT_SLOT = 1;
    private static final int ONE_BILL_SLOT = 2;
    private static final int TEN_THOUSAND_BILL_SLOT = 10;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public AutomaticPetalDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTOMATIC_PETAL_DISPENSER.get(), pos, state);
    }

    public IItemHandler getItemHandler(){
        return this.itemHandler;
    }

    public void setMenu(AbstractPetalMenu menu) {
        this.menu = menu;
    }

    private void sortInput() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);

        if (input.isEmpty() || !(input.getItem() instanceof PetalBill bill)) {
            return;
        }

        int value = bill.getValue();
        for (int i = 0; i < DENOMINATIONS.length; i++) {
            if (DENOMINATIONS[i] == value) {
                int slot = i + 2;

                ItemStack existing = itemHandler.getStackInSlot(slot);

                if (existing.isEmpty()) {
                    itemHandler.setStackInSlot(slot, input.copy());
                    itemHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
                } else if (ItemStack.isSameItem(existing, input)) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    int amountToMove = Math.min(space, input.getCount());

                    existing.grow(amountToMove);
                    input.shrink(amountToMove);

                    itemHandler.setStackInSlot(slot, existing);
                    itemHandler.setStackInSlot(INPUT_SLOT, input);
                }

                break;
            }
        }
    }

    public int getTotalDeposited() {
        int total = 0;

        for (int i  = 0; i < DENOMINATIONS.length; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i + 2);
            total += stack.getCount() * DENOMINATIONS[i];
        }

        return total;
    }

    public boolean depositContents(ServerPlayer player) {
        int amount = getTotalDeposited();

        if (amount <= 0) {
            return false;
        }

        PetalAccountManager manager = PetalAccountManager.get(player.serverLevel());
        PetalAccount account = manager.getUsableAccount(player);

        if (account == null) {
            return false;
        }

        manager.deposit(account.getAccountID(), amount);

        //Clear denomination slots
        for (int i = ONE_BILL_SLOT; i <= TEN_THOUSAND_BILL_SLOT; i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        setChanged();

        return true;
    }

    //necessary necessaries
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!pLevel.isClientSide() && !itemHandler.getStackInSlot(INPUT_SLOT).isEmpty()) {
            sortInput();
        }
        //totalDeposited = getTotalDeposited();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Petal Bank");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        PetalAccountManager manager = PetalAccountManager.get((ServerLevel) player.level());
        PetalAccount account = manager.getUsableAccount((ServerPlayer) player);
        if (account == null) {
            isPrimaryAccount = true;
            return new CreatePetalAccountMenu(containerId, inv, this);
        }
        isPrimaryAccount = false;
        return new APDMainMenu(containerId, inv, this); //change to APDMainMenu
    }
}

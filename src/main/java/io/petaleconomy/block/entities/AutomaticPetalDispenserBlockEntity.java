package io.petaleconomy.block.entities;

import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.gui.APDMainMenu;
import io.petaleconomy.item.PetalBill;
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
    private final ItemStackHandler itemHandler = new ItemStackHandler(11){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == INPUT_SLOT) {
                return isPetalBill(stack);
            }

            if (slot == CARD_SLOT) {
                return isPetalCard(stack);
            }

            if (slot >= ONE_BILL_SLOT && slot <= TEN_THOUSAND_BILL_SLOT) {
                return false;
            }

            return false;
        }
    };

    private static boolean isPetalBill(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_BILLS);
    }

    private static boolean isPetalCard(ItemStack stack) {
        return false; //until created
    }

    private static final int[] DENOMINATIONS = {
            1, 5, 10, 20, 50, 100, 500, 1000, 10000
    };

    //Deposit Menu/Screen
    private static final int CARD_SLOT = 0;
    private static final int INPUT_SLOT = 1;
    private static final int ONE_BILL_SLOT = 2;
    private static final int TEN_THOUSAND_BILL_SLOT = 10;

    private int totalDeposited = 0;
    private int accountBalance = 0;
    protected final ContainerData data;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public AutomaticPetalDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTOMATIC_PETAL_DISPENSER.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                switch (pIndex) {
                    case 0:
                        return totalDeposited;
                    /*case 1:
                        return accountBalance;*/
                    default:
                        return 0;
                }
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0:
                        totalDeposited = pValue;
                    case 1:
                        accountBalance = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
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

    public int getAccountBalance(ServerPlayer player) {
        PetalAccountManager manager = PetalAccountManager.get(player.serverLevel());
        PetalAccount account = manager.getUsableAccount(player);

        if (account == null) {
            return 0;
        }

        return account.getBalance();
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
        totalDeposited = getTotalDeposited();
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
            //return CreateAccountMenu
        }

        return new APDMainMenu(containerId, inv, this, (IItemHandler)itemHandler, this.data); //change to APDMainMenu
    }
}

package io.blossombree.petaleconomy.block.entities;

import io.blossombree.petaleconomy.economy.PetalAccount;
import io.blossombree.petaleconomy.economy.PetalAccountManager;
import io.blossombree.petaleconomy.gui.menus.APDMainMenu;
import io.blossombree.petaleconomy.gui.menus.CreateAccountMenu;
import io.blossombree.petaleconomy.gui.menus.DepositMenu;
import io.blossombree.petaleconomy.item.PetalCard;
import io.blossombree.petaleconomy.util.Constants;
import io.blossombree.petaleconomy.util.MethodUtils;
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

import java.util.UUID;

public class APDBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(12) {
        @Override
        protected void onContentsChanged (int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid (int slot, ItemStack stack) {
            if (slot == Constants.INPUT_SLOT) { //update when CreateAccount and Deposit menus are made
                return MethodUtils.isPetalBill(stack) || MethodUtils.isPetalCard(stack);
            }

            if (slot == Constants.INPUT_SLOT) {
                return MethodUtils.isPetalCard(stack);
            }

            if (slot == Constants.CARD_SLOT) {
                return MethodUtils.isPetalCard(stack);
            }

            if (slot >= Constants.ONE_BILL_SLOT && slot <= Constants.TEN_THOUSAND_BILL_SLOT) {
                return false;
            }

            return false;
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private UUID activePlayer;

    public APDBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.APD.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    public void moveCard() {
        if (!itemHandler.getStackInSlot(Constants.CARD_SLOT).isEmpty()) {
            itemHandler.setStackInSlot(11, itemHandler.getStackInSlot(Constants.CARD_SLOT));
            itemHandler.setStackInSlot(Constants.CARD_SLOT, ItemStack.EMPTY);
        }
    }

    public void returnCard() {
        if (!itemHandler.getStackInSlot(11).isEmpty()) {
            itemHandler.setStackInSlot(Constants.CARD_SLOT, itemHandler.getStackInSlot(11));
            itemHandler.setStackInSlot(11, ItemStack.EMPTY);
        }
    }


    public UUID getActivePlayer() {
        return activePlayer;
    }

    public boolean isInUse() {
        return activePlayer != null;
    }

    public boolean isUsedBy(Player player) {
        return player.getUUID().equals(activePlayer);
    }

    public boolean tryClaim(Player player) {
        if (activePlayer == null || isUsedBy(player)) {
            activePlayer = player.getUUID();
            setChanged();
            return true;
        }

        return false;
    }

    public void release() {
        activePlayer = null;
        setChanged();
    }

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

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            if (itemHandler.getStackInSlot(i).getItem() instanceof PetalCard) {
                inventory.setItem(i, itemHandler.getStackInSlot(i));
            }
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.petal_economy.petal_bank");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        PetalAccountManager manager = PetalAccountManager.get((ServerLevel) player.level());
        PetalAccount account = manager.getUsableAccount((ServerPlayer) player);

        if (account == null) {
            return new CreateAccountMenu(containerId, inv, this);
        }
        return new APDMainMenu(containerId, inv, this);
    }
}

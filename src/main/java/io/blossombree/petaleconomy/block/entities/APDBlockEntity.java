package io.blossombree.petaleconomy.block.entities;

import io.blossombree.petaleconomy.util.Constants;
import io.blossombree.petaleconomy.util.MethodUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
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
                return MethodUtils.isPetalBill(stack);
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
        for (int slot = Constants.ONE_BILL_SLOT;
             slot <= Constants.TEN_THOUSAND_BILL_SLOT;
             slot++) {

            itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        }
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

    @Override
    public Component getDisplayName() {
        return Component.literal("Petal Bank");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return null;
    }
}

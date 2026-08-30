package io.petaleconomy.gui;

import io.petaleconomy.block.AutomaticPetalDispenser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class AutomaticPetalDispenserMenu extends AbstractContainerMenu {
    public final AutomaticPetalDispenser block;
    private final Level level;
    private final ContainerData data;

    public AutomaticPetalDispenserMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this (pContainerId, inv, inv.player.level().getBlockState(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public AutomaticPetalDispenserMenu(int pContainerId, Inventory inv, Block block, ContainerData data) {
        super(, pContainerId);
        checkContainerSize(inv, 2);
        block = ((AutomaticPetalDispenser) block);
        this.level = inv.player.level();
        this.data = data;

        //player inv
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.

        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return false;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }
    }

    private void addPlayerHotbar (Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    playerInventory, col,
                    8 + col * 18,
                    142
            ));
        }
    }
}

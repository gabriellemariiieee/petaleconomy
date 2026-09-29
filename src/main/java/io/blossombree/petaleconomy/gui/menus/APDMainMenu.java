package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.block.ModBlocks;
import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class APDMainMenu extends AbstractPetalMenu {
    private final APDBlockEntity blockEntity;
    private final Level level;
    private final BlockPos blockPos;

    public APDMainMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this (containerId, inventory, (APDBlockEntity)inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public APDMainMenu(int containerId, Inventory inventory, APDBlockEntity blockEntity) {
        super(ModMenuTypes.APD_MAIN_MENU.get(), containerId, inventory.player);

        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        this.blockPos = blockEntity.getBlockPos();
        this.menuSource = PetalMenuSource.APD;
        this.sourcePos = blockPos;
        addPlayerInventory(inventory);
        addCardSlot(blockEntity.getItemHandler());

        updateCurrentAccount();
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, ModBlocks.AUTOMATIC_PETAL_DISPENSER.get());
    }
}

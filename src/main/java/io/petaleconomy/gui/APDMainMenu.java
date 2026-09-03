package io.petaleconomy.gui;

import io.petaleconomy.block.ModBlocks;
import io.petaleconomy.block.entities.AutomaticPetalDispenserBlockEntity;
import io.petaleconomy.economy.PetalAccount;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.UUID;

public class APDMainMenu extends AbstractPetalMenu {
    public final AutomaticPetalDispenserBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private IItemHandler itemHandler;

    public APDMainMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this.itemHandler = iItemHandler;
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(11), this.itemHandler);
    }

    public APDMainMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data, IItemHandler iItemHandler) {
        super(ModMenuTypes.APD_MAIN_MENU.get(), pContainerId, (ServerPlayer) inv.player, iItemHandler);
        checkContainerSize(inv, 11);
        blockEntity = ((AutomaticPetalDispenserBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);

        this.itemHandler = iItemHandler;

        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.AUTOMATIC_PETAL_DISPENSER.get());
    }
}

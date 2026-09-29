package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.block.ModBlocks;
import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.economy.PetalAccountManager;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class ManageAccessMenu extends AbstractPetalMenu{
    private final APDBlockEntity blockEntity;
    private final Level level;

    public ManageAccessMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, (APDBlockEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ManageAccessMenu(int containerId, Inventory inventory, APDBlockEntity blockEntity) {
        super(ModMenuTypes.MANAGE_ACCESS_MENU.get(), containerId, inventory.player);
        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        addCardSlot(blockEntity.getItemHandler());
        addInputSlot(26);
        addPlayerInventory(inventory);

        updateCurrentAccount();
    }

    public void addAuthorizedUser(UUID playerUUID) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PetalAccountManager manager = PetalAccountManager.get(serverPlayer.serverLevel());
        if (manager.addAuthorizedUser(currentAccount.getAccountId(), playerUUID)) {
            if (bindCard(getCurrentCard(Constants.INPUT_SLOT), currentAccount, playerUUID)) {
                if (!serverPlayer.getInventory().add(getCurrentCard(Constants.INPUT_SLOT))) {
                    serverPlayer.drop(getCurrentCard(Constants.INPUT_SLOT), false);
                }
                itemHandler.setStackInSlot(Constants.INPUT_SLOT, ItemStack.EMPTY);
            }
        }
    }

    public void removeAuthorizedUser(UUID playerUUID) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PetalAccountManager manager = PetalAccountManager.get(serverPlayer.serverLevel());
        manager.removeAuthorizedUser(currentAccount.getAccountId(), playerUUID);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, ModBlocks.AUTOMATIC_PETAL_DISPENSER.get());
    }
}

package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.block.ModBlocks;
import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.economy.PetalAccount;
import io.blossombree.petaleconomy.economy.PetalAccountManager;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ManageAccountMenu extends AbstractPetalMenu {
    private final APDBlockEntity blockEntity;
    private final Level level;

    public ManageAccountMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, (APDBlockEntity)inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ManageAccountMenu(int containerId, Inventory inventory, APDBlockEntity blockEntity) {
        super(ModMenuTypes.MANAGE_ACCOUNT_MENU.get(), containerId, inventory.player);

        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        this.menuSource = PetalMenuSource.APD;
        this.sourcePos = blockEntity.getBlockPos();

        addPlayerInventory(inventory);
        addCardSlot(blockEntity.getItemHandler());
        addInputSlot(62);

        updateCurrentAccount();
    }

    public boolean manageAccount(String accountName, int flowerColor) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        PetalAccountManager manager = PetalAccountManager.get(serverPlayer.serverLevel());

        PetalAccount account = getCurrentAccount();
        if (!accountName.isEmpty()) {
            manager.setAccountName(account.getAccountId(), accountName);
        }

        ItemStack newCard = getCurrentCard(Constants.INPUT_SLOT);
        ItemStack currentCard = getCurrentCard(Constants.CARD_SLOT);

        if (!newCard.isEmpty()) {
            if (!manager.validateCard(newCard)) {
                return false;
            }
             if(bindCard(newCard, account)) {
                 if (!serverPlayer.getInventory().add(newCard)) {
                     serverPlayer.drop(newCard, false);
                 }
                 itemHandler.setStackInSlot(Constants.INPUT_SLOT, ItemStack.EMPTY);
             }
            setCardFlowerColor(newCard, flowerColor);
        } else if (!currentCard.isEmpty()) {
            setCardFlowerColor(currentCard, flowerColor);
        }

        return true;
    }

    public boolean deleteAccount(String accountName) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        PetalAccountManager manager = PetalAccountManager.get(serverPlayer.serverLevel());

        if (!accountName.equals(currentAccount.getAccountName())) {
            return false;
        }

        return manager.deleteAccount(currentAccount.getAccountId(), serverPlayer);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, ModBlocks.AUTOMATIC_PETAL_DISPENSER.get());
    }
}

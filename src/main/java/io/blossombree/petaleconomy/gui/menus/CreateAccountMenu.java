package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.block.ModBlocks;
import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.capabilities.PetalCapabilities;
import io.blossombree.petaleconomy.economy.PendingTransaction;
import io.blossombree.petaleconomy.economy.PetalAccount;
import io.blossombree.petaleconomy.economy.PetalAccountManager;
import io.blossombree.petaleconomy.economy.PrimaryPetalAccount;
import io.blossombree.petaleconomy.util.Constants;
import io.blossombree.petaleconomy.util.TransactionType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateAccountMenu extends AbstractPetalMenu {
    private final APDBlockEntity blockEntity;
    private final Level level;
    private final boolean fromMainMenu;
    private PrimaryPetalAccount primaryPetalAccount;

    public CreateAccountMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this (containerId, inventory, (APDBlockEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()), extraData.readBoolean());
    }

    public CreateAccountMenu(int containerId, Inventory inventory, APDBlockEntity blockEntity) {
        this (containerId, inventory, blockEntity, false);
    }

    public CreateAccountMenu(int containerId, Inventory inventory, APDBlockEntity blockEntity, boolean fromMainMenu) {
        super(ModMenuTypes.CREATE_ACCOUNT_MENU.get(), containerId, inventory.player);

        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        this.menuSource = PetalMenuSource.APD;
        this.sourcePos = blockEntity.getBlockPos();
        this.fromMainMenu = fromMainMenu;

        addPlayerInventory(inventory);
        addCardSlot(blockEntity.getItemHandler(), 26, 52);
        addInputSlot(80);

        restorePendingTransaction();
        syncPendingTransactionAmount();
    }

    public boolean createAccount(String name) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        PetalAccountManager manager = PetalAccountManager.get(serverPlayer.serverLevel());

        ItemStack card = getCurrentCard(0);

        primaryPetalAccount = serverPlayer.getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT).orElseThrow(() -> new IllegalStateException("Not found"));
        boolean primaryAccount = !primaryPetalAccount.hasPrimaryAccount();

        if (card.isEmpty() && !primaryAccount) {
            return false;
        }

        if (!card.isEmpty() && !manager.canCreateAccountFromCard(card, serverPlayer)) {
            return false;
        }

        PetalAccount account = manager.createAccount(serverPlayer.getUUID());
        UUID accountId = account.getAccountId();

        manager.setAccountName(accountId, name);
        if (primaryAccount) {
            primaryPetalAccount.setAccountId(accountId);
        }

        if (!card.isEmpty()) {
            bindCard(card, account);

            if (!serverPlayer.getInventory().add(card)) {
                serverPlayer.drop(card, false);
            }
            itemHandler.setStackInSlot(Constants.CARD_SLOT, ItemStack.EMPTY);
        }

        PendingTransaction pending = getPendingTransaction();

        if (pending != null && pending.getAmount() > 0) {
            manager.deposit(accountId, pending.getAmount());
        }

        clearPendingTransaction();

        return true;
    }

    public void ejectStartingBalance() {
        returnPendingTransactionItems();
        updatePendingTransactionFromSlots();
    }

    @Override
    protected void onInputChanged(ItemStack previousInput, ItemStack newInput) {
        if (!hasPendingTransaction()) {
            createPendingTransaction(TransactionType.CREATE_ACCOUNT, null, 0, new HashMap<Integer, Integer>());
        }
        sortInput();
    }

    @Override
    protected void onCardChanged(ItemStack previousCard, ItemStack newCard) {

    }

    public boolean isFromMainMenu() {
        return fromMainMenu;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, ModBlocks.AUTOMATIC_PETAL_DISPENSER.get());
    }
}

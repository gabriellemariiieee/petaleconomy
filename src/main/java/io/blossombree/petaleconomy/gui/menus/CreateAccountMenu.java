package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.capabilities.PetalCapabilities;
import io.blossombree.petaleconomy.economy.PendingTransaction;
import io.blossombree.petaleconomy.economy.PetalAccount;
import io.blossombree.petaleconomy.economy.PetalAccountManager;
import io.blossombree.petaleconomy.economy.PrimaryPetalAccount;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class CreateAccountMenu extends AbstractPetalMenu {
    private final APDBlockEntity blockEntity;
    private final Level level;

    public CreateAccountMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this (containerId, inventory, (APDBlockEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CreateAccountMenu(int containerId, Inventory inventory, APDBlockEntity blockEntity) {
        super(ModMenuTypes.CREATE_ACCOUNT_MENU.get(), containerId, inventory.player);

        this.blockEntity = blockEntity;
        this.level = inventory.player.level();

        addPlayerInventory(inventory);
        addCardSlot(blockEntity.getItemHandler(), 26, 52);
        addInputSlot(80);
    }

    public void createAccount(String name) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PetalAccountManager manager = PetalAccountManager.get(serverPlayer.serverLevel());

        ItemStack card = getCurrentCard();

        PrimaryPetalAccount primaryPetalAccount = serverPlayer.getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT).orElseThrow(() -> new IllegalStateException("Not found"));
        boolean primary = primaryPetalAccount.hasPrimaryAccount();

        if (card.isEmpty() && !primary) {
            return;
        }

        if (!card.isEmpty() && !manager.canCreateAccountFromCard(card, serverPlayer)) {
            return;
        }

        PetalAccount account = manager.createAccount(serverPlayer.getUUID());
        UUID accountId = account.getAccountId();

        manager.setAccountName(accountId, name);
        if (primary) {
            primaryPetalAccount.setAccountId(accountId);
        }

        if (!card.isEmpty()) {
            bindCard(card, account);
        }

        PendingTransaction pending = getPendingTransaction();

        if (pending != null && pending.getAmount() > 0) {
            manager.deposit(accountId, pending.getAmount());
        }

        clearPendingTransaction();
    }

    public void ejectStartingBalance() {
        returnPendingTransactionItems();

        removePendin
    }
}

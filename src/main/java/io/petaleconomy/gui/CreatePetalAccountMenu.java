package io.petaleconomy.gui;

import io.petaleconomy.block.ModBlocks;
import io.petaleconomy.block.entities.AutomaticPetalDispenserBlockEntity;
import io.petaleconomy.capabilities.PetalCapabilities;
import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.economy.PrimaryPetalAccount;
import io.petaleconomy.events.PetalCapabilitiesEvents;
import io.petaleconomy.item.ModItems;
import io.petaleconomy.item.PetalCard;
import io.petaleconomy.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public class CreatePetalAccountMenu extends AbstractPetalMenu {
    public final AutomaticPetalDispenserBlockEntity blockEntity;
    private final Level level;
    private boolean primaryAccount = false;

    public CreatePetalAccountMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CreatePetalAccountMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.CREATE_PETAL_ACCOUNT_MENU.get(), pContainerId, inv.player);
        blockEntity = ((AutomaticPetalDispenserBlockEntity) entity);
        this.level = inv.player.level();

        blockEntity.setMenu(this);
        addPlayerInventory(inv);
        addCardSlot(blockEntity.getItemHandler(), 26, 52);
        addInputSlot(80);
        this.primaryAccount = blockEntity.isPrimaryAccount;
    }

    public void createAccount(String name) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PetalAccountManager manager = PetalAccountManager.get(serverPlayer.serverLevel());
        ItemStack card = getCurrentCard();
        PrimaryPetalAccount primaryPetalAccount = serverPlayer.getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT).orElseThrow(() -> new IllegalStateException("Not found"));
        boolean primary = primaryPetalAccount.hasPrimaryAccount();

        //if no card and no primary account return
        if (card.isEmpty() && !(primary)) {
            return;
        }
        //if card is present, make sure it can be bound
        if (!card.isEmpty() && !manager.canCreateAccountFromCard(card, serverPlayer)) {
            return;
        }

        //makes account
        PetalAccount account = manager.createAccount(serverPlayer.getUUID());
        UUID newAccountId = account.getAccountID();
        manager.setAccountName(newAccountId, name);

        if (primary) {
            primaryPetalAccount.setAccountId(newAccountId);
        }

        bindCard(card, account);
        depositContents(this.blockEntity);
    }

    public void ejectStartingBalance() {
        SimpleContainer container = new SimpleContainer(Constants.DENOMINATIONS.length);
        for (int i = Constants.ONE_BILL_SLOT; i <= Constants.TEN_THOUSAND_BILL_SLOT; i++) {
            int slot = i;
            ItemStack existing = itemHandler.getStackInSlot(slot);
            if (existing != null) {
                int inv = player.getInventory().getFreeSlot();
                int currentInvSlot = player.getInventory().findSlotMatchingItem(existing);
                if (inv != 0) {
                    player.getInventory().add(inv, existing);
                } else if (currentInvSlot != 0) {
                    player.getInventory().add(currentInvSlot, existing);
                } else {
                    container.addItem(existing);
                }
            }
        }
        Containers.dropContents(level, blockEntity.getBlockPos(), container);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(pPlayer.level(), blockEntity.getBlockPos()), pPlayer, ModBlocks.AUTOMATIC_PETAL_DISPENSER.get());
    }
}

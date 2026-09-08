package io.petaleconomy.gui;

import io.petaleconomy.block.ModBlocks;
import io.petaleconomy.block.entities.AutomaticPetalDispenserBlockEntity;
import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.item.ModItems;
import io.petaleconomy.item.PetalCard;
import io.petaleconomy.network.PetalNetwork;
import io.petaleconomy.network.SyncAccountNamePacket;
import io.petaleconomy.util.Constants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;

public abstract class AbstractPetalMenu extends AbstractContainerMenu {

    protected final Player player;
    protected ItemStackHandler itemHandler;
    protected PetalAccount currentAccount;
    protected int accountBalance;
    private String currentAccountName = "";
    protected ContainerData data;

    private String lastSyncedAccountName = null;

    protected AbstractPetalMenu(@Nullable MenuType<?> menuType, int containerId, Player player) {
        super(menuType, containerId);
        this.player = player;

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                if (pIndex == 0) {
                    return accountBalance;
                }

                return 0;
            }

            @Override
            public void set(int pIndex, int value) {
                if (pIndex == 0) {
                    accountBalance = value;
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        };

        addDataSlots(data);
    }

    //inventory helper method
    protected void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }

        //Hotbar
        for(int c = 0; c < 9; c++) {
            this.addSlot(new Slot(playerInventory, c, 8  + c * 18, 162));
        }
    }

    protected void addCardSlot(ItemStackHandler itemHandler) {
        this.itemHandler = itemHandler;
        this.addSlot(new SlotItemHandler(itemHandler, Constants.CARD_SLOT, Constants.CARD_SLOT_X, Constants.CARD_SLOT_Y));
    }

    //for create account menu
    protected void addCardSlot(ItemStackHandler itemHandler, int x, int y) {
        this.itemHandler = itemHandler;
        this.addSlot(new SlotItemHandler(itemHandler, Constants.CARD_SLOT, x, y));
    }

    protected void addInputSlot(int x) {
        this.addSlot(new SlotItemHandler(itemHandler, Constants.INPUT_SLOT, x, 52));
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        //check if the slot clicked is a vanilla container slot
        if (index < Constants.BE_INVENTORY_FIRST_SLOT_INDEX) {
            //this is a vanilla container slot so merge into block entity inventory
            if(!moveItemStackTo(sourceStack, Constants.BE_INVENTORY_FIRST_SLOT_INDEX, Constants.BE_INVENTORY_FIRST_SLOT_INDEX + 2, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < Constants.BE_INVENTORY_FIRST_SLOT_INDEX + Constants.BE_INVENTORY_SLOT_COUNT) {
            //this is a block entity inventory slot so merge the stack into vanilla container inventory
            if(!moveItemStackTo(sourceStack, Constants.VANILLA_FIRST_SLOT_INDEX, Constants.BE_INVENTORY_FIRST_SLOT_INDEX, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex: " + index);
        }

        //if stack size == 0 (the entire stack was moved) set slot contents to null
        if(sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(pPlayer, sourceStack);
        return copyOfSourceStack;
    }

    protected ItemStack getCurrentCard() {
        return itemHandler.getStackInSlot(Constants.CARD_SLOT);
    }

    protected PetalAccount getCurrentAccount() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return null;
        }
        ItemStack card = getCurrentCard();

        PetalAccountManager manager = PetalAccountManager.get(serverPlayer.serverLevel());

        if(!card.isEmpty()) {
            return currentAccount = manager.getAccountForCard(card, serverPlayer);
        } else {
            return currentAccount = manager.getUsableAccount(serverPlayer);
        }
    }

    public void updateCurrentAccount() {
        currentAccount = getCurrentAccount();
        accountBalance = currentAccount != null ? currentAccount.getBalance() : 0;

        syncAccountName();
    }

    protected boolean depositContents(AutomaticPetalDispenserBlockEntity entity) {
        if (!(player instanceof ServerPlayer player)) {
            return false;
        }
        int amount = entity.getTotalDeposited();

        if (amount <= 0) {
            return false;
        }

        PetalAccountManager manager = PetalAccountManager.get(player.serverLevel());

        if (currentAccount == null) {
            return false;
        }

        manager.deposit(currentAccount.getAccountID(), amount);
        this.accountBalance = currentAccount.getBalance();
        entity.clearDenomSlots();

        return true;
    }

    protected void bindCard(ItemStack card, PetalAccount account) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ModItems.PETAL_CARD.get().setAccountId(card, account.getAccountID());
        ModItems.PETAL_CARD.get().setBoundPlayerUUID(card, serverPlayer.getUUID());
    }

    //for abstract screen
    private void syncAccountName() {
        if (player instanceof ServerPlayer serverPlayer) {
            String accountName = currentAccount != null ? currentAccount.getAccountName() : "";

            PetalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncAccountNamePacket(accountName));
        }
    }

    public void setCurrentAccountName(String name) {
        this.currentAccountName = name;
    }

    protected String getCurrentAccountName() {
        return this.currentAccountName;
    }

    public void openPetalMenu() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        NetworkHooks.openScreen(serverPlayer, player.level().get) {

        }
    }

    private int getData(int index) {
        return data.get(index);
    }

    protected int getCurrentBalance() {
        return getData(0);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if(player instanceof ServerPlayer) {
            String accountName = currentAccount != null ? currentAccount.getAccountName() : "";

            if (!accountName.equals(lastSyncedAccountName)) {
                lastSyncedAccountName = accountName;
                syncAccountName();
            }
        }
    }
}

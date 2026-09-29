package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.economy.*;
import io.blossombree.petaleconomy.item.ModItems;
import io.blossombree.petaleconomy.item.PetalBill;
import io.blossombree.petaleconomy.item.PetalCard;
import io.blossombree.petaleconomy.network.AuthorizedUsersPacket;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.network.SyncAccountNamePacket;
import io.blossombree.petaleconomy.util.Constants;
import io.blossombree.petaleconomy.util.TransactionType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

public abstract class AbstractPetalMenu extends AbstractContainerMenu {
    protected final Player player;

    protected PetalAccount currentAccount;
    protected int accountBalance;
    protected int transactionAmount;
    protected boolean accountOwner;
    protected List<UUID> authorizedUsers;

    private String currentAccountName = "";
    private String lastSyncedAccountName = null;
    private PetalAccountManager manager;
    private PendingTransactionData transactionData;
    private ItemStack card = ItemStack.EMPTY;

    protected ContainerData data;
    protected ItemStackHandler itemHandler;
    protected ItemStack lastInputStack = ItemStack.EMPTY;

    protected PetalMenuSource menuSource;
    protected BlockPos sourcePos;
    protected int sourceSlot = -1;

    protected AbstractPetalMenu(@Nullable MenuType<?> menuType, int containerId, Player player) {
        super (menuType, containerId);
        this.player = player;
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == 0) {
                    return accountBalance;
                }
                if (index == 1) {
                    return transactionAmount;
                }
                if (index == 2) {
                    return accountOwner ? 1 : 0;
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    accountBalance = value;
                }
                if (index == 1) {
                    transactionAmount = value;
                }
                if (index == 2) {
                    accountOwner = value != 0;
                }
            }

            @Override
            public int getCount() {
                return 3;
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

    //for deposit menu
    protected void addInputSlot(int x, int y) {
        this.addSlot(new SlotItemHandler(itemHandler, Constants.INPUT_SLOT, x, y));
    }

    protected void readMenuSource(FriendlyByteBuf extraData) {
        menuSource = extraData.readEnum(PetalMenuSource.class);
        switch (menuSource) {
            case APD -> sourcePos = extraData.readBlockPos();
            case PORTABLE_APD -> sourceSlot = extraData.readInt();
        }
    }

    public PetalMenuSource getMenuSource() {
        return this.menuSource;
    }

    public BlockPos getSourcePos() {
        return this.sourcePos;
    }

    public int getSourceSlot() {
        return this.sourceSlot;
    }

    protected void releaseMenuSource() {
        if (menuSource == null) {
            return;
        }

        switch (menuSource) {
            case APD -> {
                if (player.level().getBlockEntity(sourcePos) instanceof APDBlockEntity blockEntity) {
                    if (blockEntity.isUsedBy(player)) {
                        blockEntity.release();
                    }
                }
            }
            case PORTABLE_APD -> {

            }
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        clearDenomSlots();
        releaseMenuSource();
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

    protected ItemStack createPetalBills(int denomination, int quantity) {
        PetalBill bill = switch (denomination) {
            case 1 -> ModItems.ONE_PETAL_BILL.get();
            case 5 -> ModItems.FIVE_PETALS_BILL.get();
            case 10 -> ModItems.TEN_PETALS_BILL.get();
            case 20 -> ModItems.TWENTY_PETALS_BILL.get();
            case 50 -> ModItems.FIFTY_PETALS_BILL.get();
            case 100 -> ModItems.ONE_HUNDRED_PETALS_BILL.get();
            case 500 -> ModItems.FIVE_HUNDRED_PETALS_BILL.get();
            case 1000 -> ModItems.ONE_THOUSAND_PETALS_BILL.get();
            case 10000 -> ModItems.TEN_THOUSAND_PETALS_BILL.get();
            default -> throw new IllegalArgumentException("Invalid Petal denomination: " + denomination);
        };

        return new ItemStack(bill, quantity);
    }

    protected void sortInput() {
        ItemStack input = itemHandler.getStackInSlot(Constants.INPUT_SLOT);

        if (input.isEmpty() || input.getItem() instanceof PetalCard) {
            return;
        }

        int value = ((PetalBill) input.getItem()).getValue();

        for (int i = 0; i < Constants.DENOMINATIONS.length; i++) {
            if (Constants.DENOMINATIONS[i] == value) {
                int slot = i + 2;
                ItemStack existing = itemHandler.getStackInSlot(slot);

                if (existing.isEmpty()) {
                    itemHandler.setStackInSlot(slot, input.copy());
                    itemHandler.setStackInSlot(Constants.INPUT_SLOT, ItemStack.EMPTY);
                } else if (ItemStack.isSameItem(existing, input)) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    int amountToMove = Math.min(space, input.getCount());

                    existing.grow(amountToMove);
                    input.shrink(amountToMove);

                    itemHandler.setStackInSlot(slot, existing);
                    itemHandler.setStackInSlot(Constants.INPUT_SLOT, input);
                }

                break;
            }
        }

        updatePendingTransactionFromSlots();
    }

    protected void clearDenomSlots() {
        if (itemHandler == null) {
            return;
        }

        for (int i = Constants.ONE_BILL_SLOT; i <= Constants.TEN_THOUSAND_BILL_SLOT; i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    protected void updatePendingTransactionFromSlots() {
        updatePendingAmount(getTransactionTotal());

        for (int i = 0; i < Constants.DENOMINATIONS.length; i++) {
            int denomination = Constants.DENOMINATIONS[i];
            int slot = i + 2;
            int quantity = itemHandler.getStackInSlot(slot).getCount();

            updatePendingDenomination(denomination, quantity);
        }
    }

    protected int getTransactionTotal() {
        int total = 0;

        for (int i = 0; i < Constants.DENOMINATIONS.length; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i + 2);
            total += stack.getCount() * Constants.DENOMINATIONS[i];
        }

        return total;
    }

    public ItemStack getCurrentCard(int slot) {
        if (slot == 0) {
            return itemHandler.getStackInSlot(Constants.CARD_SLOT);
        } else if (slot == 1) {
            return itemHandler.getStackInSlot(Constants.INPUT_SLOT);
        }
        return null;
    }

    protected boolean isCurrentCardUsable() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        card = getCurrentCard(0);

        if(card.isEmpty()) {
            return false;
        }
        manager = PetalAccountManager.get(serverPlayer.serverLevel());

        return manager.isCardValid(card, serverPlayer);
    }

    protected PetalAccount getCurrentAccount() {
        if(!(player instanceof ServerPlayer serverPlayer)) {
            return null;
        }

        manager = PetalAccountManager.get(serverPlayer.serverLevel());

        card = getCurrentCard(Constants.CARD_SLOT);
        if(!card.isEmpty()) {
            return currentAccount = manager.getAccountForCard(card, serverPlayer);
        }

        if (manager.getPrimaryAccount(serverPlayer) != null) {
            return currentAccount = manager.getPrimaryAccount(serverPlayer);
        }

        return currentAccount = null;
    }

    protected boolean bindCard (ItemStack card, PetalAccount account) {
        if(!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        manager = PetalAccountManager.get(serverPlayer.serverLevel());

        if(!manager.canCreateAccountFromCard(card, serverPlayer)) {
            return false;
        }

        ModItems.PETAL_CARD.get().setAccountId(card, account.getAccountId());
        ModItems.PETAL_CARD.get().setBoundPlayer(card, serverPlayer.getUUID());

        return true;
    }

    protected boolean bindCard(ItemStack card, PetalAccount account, UUID playerUUID) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        manager = PetalAccountManager.get(serverPlayer.serverLevel());
        if (!manager.validateCard(card)) {
            return false;
        }

        ModItems.PETAL_CARD.get().setAccountId(card, account.getAccountId());
        ModItems.PETAL_CARD.get().setBoundPlayer(card, playerUUID);

        manager.validateCard(card);

        return true;
    }

    protected void setCardFlowerColor(ItemStack card, int flowerColor) {
        ModItems.PETAL_CARD.get().setFlowerColor(card, flowerColor);
    }

    protected PendingTransaction getPendingTransaction() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return null;
        }

        transactionData = PendingTransactionData.get(serverPlayer.serverLevel());

        return transactionData.getPendingTransaction(serverPlayer.getUUID());
    }

    protected boolean hasPendingTransaction() {
        return getPendingTransaction() != null;
    }

    protected void restorePendingTransaction() {
        PendingTransaction pending = getPendingTransaction();

        if (pending == null) {
            return;
        }

        transactionAmount = pending.getAmount();

        for (Map.Entry<Integer, Integer> entry : pending.getDenominations().entrySet()) {
            int denomination = entry.getKey();
            int quantity = entry.getValue();

            if (quantity <= 0) {
                continue;
            }

            ItemStack bills = createPetalBills(denomination, quantity);

            itemHandler.setStackInSlot(Arrays.binarySearch(Constants.DENOMINATIONS, denomination) + 2, bills);
        }
    }

    protected void clearPendingTransaction() {
        if(!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        transactionData = PendingTransactionData.get(serverPlayer.serverLevel());
        transactionData.removePendingTransaction(serverPlayer.getUUID());
    }

    protected void createPendingTransaction (TransactionType type, @Nullable UUID accountId, int amount, Map<Integer, Integer> denominations) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PendingTransaction transaction = new PendingTransaction(serverPlayer.getUUID(), accountId, type, amount, denominations);
        transactionData = PendingTransactionData.get(serverPlayer.serverLevel());

        transactionData.setPendingTransaction(serverPlayer.getUUID(), transaction);
    }

    protected void updatePendingTransaction(UUID accountId) {
        transactionData = PendingTransactionData.get(((ServerPlayer)player).serverLevel());
        transactionData.setAccountId(((ServerPlayer)player).getUUID(), accountId);
    }

    protected void updatePendingAmount(int amount) {
        transactionData = PendingTransactionData.get(((ServerPlayer)player).serverLevel());
        transactionData.setAmount(((ServerPlayer) player).getUUID(), amount);

        transactionAmount = amount;
    }

    protected void updatePendingDenomination(int denomination, int quantity) {
        transactionData = PendingTransactionData.get(((ServerPlayer)player).serverLevel());
        transactionData.setDenomination(((ServerPlayer) player).getUUID(), denomination, quantity);
    }

    protected void returnPendingTransactionItems() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        for (int i = Constants.ONE_BILL_SLOT; i <= Constants.TEN_THOUSAND_BILL_SLOT; i ++) {
            ItemStack bills = itemHandler.getStackInSlot(i);

            if (!bills.isEmpty()) {
                if (!serverPlayer.getInventory().add(bills)) {
                    serverPlayer.drop(bills, false);
                }
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }


        /*PendingTransaction transaction = getPendingTransaction();

        if (transaction == null) {
            return;
        }

        for (Map.Entry<Integer, Integer> entry : transaction.getDenominations().entrySet()) {
            ItemStack bills = createPetalBills(entry.getKey(), entry.getValue());

            if (!serverPlayer.getInventory().add(bills)) {
                serverPlayer.drop(bills, false);
            }
        }*/
    }

    protected void cancelPendingTransaction() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (getPendingTransaction() == null) {
            return;
        }

        returnPendingTransactionItems();
        clearPendingTransaction();
    }

    protected boolean isPendingAccountValid() {
        PendingTransaction transaction = getPendingTransaction();

        if(transaction == null || transaction.getAccountId() == null) {
            return false;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        manager = PetalAccountManager.get(serverPlayer.serverLevel());
        UUID accountId = transaction.getAccountId();
        PetalAccount account = manager.getAccount(accountId);

        if (account == null) {
            return false;
        }

        return manager.isAuthorizedUser(accountId, serverPlayer.getUUID());
    }

    protected boolean pendingTransactionRequiresAccount() {
        PendingTransaction transaction = getPendingTransaction();
        if (transaction == null) {
            return false;
        }

        return transaction.getType() != TransactionType.CREATE_ACCOUNT;
    }

    protected boolean canResumePendingTransaction() {
        PendingTransaction transaction = getPendingTransaction();

        if (transaction == null) {
            return false;
        }

        if (transaction.getType() == TransactionType.CREATE_ACCOUNT) {
            return true;
        }

        return isPendingAccountValid();
    }

    protected boolean depositPendingTransaction() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        PendingTransaction transaction = getPendingTransaction();

        if (transaction == null || transaction.getType() != TransactionType.DEPOSIT) {
            return false;
        }

        if (!isPendingAccountValid()) {
            return false;
        }

        int amount = transaction.getAmount();

        if (amount <= 0) {
            return false;
        }

        manager = PetalAccountManager.get(serverPlayer.serverLevel());
        manager.deposit(transaction.getAccountId(), amount);

        clearPendingTransaction();
        updateCurrentAccount();

        return true;
    }

    protected boolean withdrawPendingTransaction() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        PendingTransaction transaction = getPendingTransaction();

        if (transaction == null || transaction.getType() != TransactionType.WITHDRAW) {
            return false;
        }

        if (!isPendingAccountValid()) {
            return false;
        }

        int amount = transaction.getAmount();

        if (amount <= 0) {
            return false;
        }

        manager = PetalAccountManager.get(serverPlayer.serverLevel());
        return manager.withdraw(transaction.getAccountId(), amount);
    }

    protected void finishPendingTransaction() {
        clearPendingTransaction();
        updateCurrentAccount();
    }

    public void updateCurrentAccount() {
        currentAccount = getCurrentAccount();
        accountBalance = currentAccount != null ? currentAccount.getBalance() : 0;
        accountOwner = currentAccount != null && currentAccount.getOwner().equals(player.getUUID());
        authorizedUsers = currentAccount != null ? currentAccount.getAuthorizedUsers().stream().toList() : new ArrayList<>(List.of(player.getUUID()));

        syncAccountName();
        syncAuthorizedUsers();
    }

    private void syncAccountName() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        String accountName = currentAccount != null ? currentAccount.getAccountName() : "";

        PetalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncAccountNamePacket(accountName));
    }

    private void syncAuthorizedUsers() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PetalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new AuthorizedUsersPacket(authorizedUsers));
    }

    public List<UUID> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAuthorizedUsers(List<UUID> authorizedUsers) {
        this.authorizedUsers.clear();
        this.authorizedUsers.addAll(authorizedUsers);
    }

    protected void syncPendingTransactionAmount() {
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        transactionAmount = getTransactionAmount();
    }

    public void setCurrentAccountName(String name) {
        this.currentAccountName = name;
    }

    public String getCurrentAccountName() {
        return this.currentAccountName;
    }

    public int getCurrentBalance() {
        return data.get(0);
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public int getPendingBalance() {
        PendingTransaction pending = getPendingTransaction();
        if (pending == null) {
            return 0;
        }

        if (pending.getType() == TransactionType.WITHDRAW) {
            return accountBalance - getTransactionAmount();
        }
        return accountBalance + getTransactionAmount();
    }

    protected void onInputChanged(ItemStack previousInput, ItemStack newInput) {

    }

    protected void onCardChanged(ItemStack previousCard, ItemStack newCard) {
        updateCurrentAccount();
    }

    public boolean isAccountOwner() {
        return accountOwner;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        ItemStack input = itemHandler.getStackInSlot(Constants.INPUT_SLOT);
        if (!ItemStack.matches(input, lastInputStack)) {
            ItemStack previousInput = lastInputStack;
            lastInputStack = input.copy();
            onInputChanged(previousInput, input);
        }

        ItemStack cardSlot = itemHandler.getStackInSlot(Constants.CARD_SLOT);
        if (!ItemStack.matches(cardSlot, card)) {
            ItemStack previousCard = card;
            card = cardSlot.copy();
            onCardChanged(previousCard, cardSlot);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            String accountName = currentAccount != null ? currentAccount.getAccountName() : "";
            if (!accountName.equals(lastSyncedAccountName)) {
                lastSyncedAccountName = accountName;
                syncAccountName();
                syncAuthorizedUsers();
            }
        }
    }
}

package io.blossombree.petaleconomy.economy;

import io.blossombree.petaleconomy.util.Constants;
import io.blossombree.petaleconomy.util.TransactionType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PendingTransactionData extends SavedData {
    private final Map<UUID, PendingTransaction> pendingTransactions = new HashMap<>();

    public PendingTransaction getPendingTransaction(UUID playerUUID) {
        return pendingTransactions.get(playerUUID);
    }

    public boolean hasPendingTransaction(UUID playerUUID) {
        return pendingTransactions.containsKey(playerUUID);
    }

    public void setPendingTransaction(UUID playerUUID, UUID accountId, TransactionType type, int amount, HashMap<Integer, Integer> denominations) {
        pendingTransactions.put(playerUUID, new PendingTransaction(playerUUID,accountId, type, amount, denominations));
        setDirty();
    }

    public void removePendingTransaction(UUID playerUUID) {
        pendingTransactions.remove(playerUUID, getPendingTransaction(playerUUID));
        setDirty();
    }

    //pending transaction management
    public void setAccountId(UUID playerUUID, UUID accountId) {
        PendingTransaction pendingTransaction = getPendingTransaction(playerUUID);
        pendingTransaction.setAccountId(accountId);

        setDirty();
    }

    public void setAmount(UUID playerUUID, int amount) {
        PendingTransaction pendingTransaction = getPendingTransaction(playerUUID);
        pendingTransaction.setAmount(amount);

        setDirty();
    }

    public void setDenomination(UUID playerUUID, int denomination, int quantity) {
        PendingTransaction pendingTransaction = getPendingTransaction(playerUUID);
        pendingTransaction.setDenomination(denomination, quantity);

        setDirty();
    }

    public void addDenomination(UUID playerUUID, int denomination) {
        PendingTransaction pendingTransaction = getPendingTransaction(playerUUID);
        pendingTransaction.setDenomination(denomination, pendingTransaction.getDenomination(denomination) + 1);

        setDirty();
    }

    public void subtractDenomination(UUID playerUUID, int denomination) {
        PendingTransaction pendingTransaction = getPendingTransaction(playerUUID);
        pendingTransaction.setDenomination(denomination, pendingTransaction.getDenomination(denomination) - 1);

        setDirty();
    }

    public void clearDenominations(UUID playerUUID) {
        PendingTransaction pendingTransaction = getPendingTransaction(playerUUID);
        pendingTransaction.clearDenominations();

        setDirty();
    }

    //persistence
    public static PendingTransactionData load(CompoundTag tag) {
        PendingTransactionData data = new PendingTransactionData();

        ListTag pendingList = tag.getList("PendingTransactions", Tag.TAG_COMPOUND);
        for (Tag transactionTag : pendingList) {
            CompoundTag transactionData = (CompoundTag) transactionTag;

            UUID playerUUID = transactionData.getUUID(Constants.PLAYERUUID);
            UUID accountId = transactionData.getUUID(Constants.ACCOUNT_ID);
            TransactionType type = TransactionType.byName(transactionData.getString("TransactionType"));
            int amount = transactionData.getInt(Constants.ACCOUNT_BALANCE);

            ListTag denominations = transactionData.getList("Denominations", Tag.TAG_COMPOUND);
            Map<Integer, Integer> denoms = new HashMap<>();
            for (int i = 0; i < denominations.size(); i++) {
                CompoundTag denominationTag = denominations.getCompound(i);

                int denomination = denominationTag.getInt("Denomination");
                int quantity = denominationTag.getInt("Quantity");

                denoms.put(denomination, quantity);
            }

            PendingTransaction pendingTransaction = new PendingTransaction(playerUUID, accountId, type, amount, denoms);
            data.pendingTransactions.put(playerUUID, pendingTransaction);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag pendingList = new ListTag();

        for (PendingTransaction pending : pendingTransactions.values()) {
            CompoundTag transactionData = new CompoundTag();

            transactionData.putUUID(Constants.PLAYERUUID, pending.getPlayerUUID());
            transactionData.putUUID(Constants.ACCOUNT_ID, pending.getAccountId());
            transactionData.putString("TransactionType", pending.getType().toString());
            transactionData.putInt(Constants.ACCOUNT_BALANCE, pending.getAmount());

            ListTag denominations = new ListTag();
            for (Map.Entry<Integer, Integer> entry : pending.getDenominations().entrySet()) {
                CompoundTag denomTag = new CompoundTag();

                denomTag.putInt("Denomination", entry.getKey());
                denomTag.putInt("Quantity", entry.getValue());

                denominations.add(denomTag);
            }
            transactionData.put("Denominations", denominations);

            pendingList.add(transactionData);
        }

        tag.put("PendingTransactions", pendingList);

        return tag;
    }

    public static PendingTransactionData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(ServerLevel.OVERWORLD);

        return overworld.getDataStorage().computeIfAbsent(
                PendingTransactionData::load,
                PendingTransactionData::new,
                "pending_transactions"
        );
    }
}

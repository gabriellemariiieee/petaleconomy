package io.blossombree.petaleconomy.economy;

import io.blossombree.petaleconomy.util.TransactionType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PendingTransaction {
    private final UUID playerUUID;
    private @Nullable UUID accountId;
    private TransactionType type;
    private int amount;
    private final Map<Integer, Integer> denominations;

    public PendingTransaction(UUID playerUUID, @Nullable UUID accountId, TransactionType type, int amount, Map<Integer, Integer> denominations) {
        this.playerUUID = playerUUID;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.denominations = new HashMap<>(denominations);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Map<Integer, Integer> getDenominations() {
        return denominations;
    }

    public void setDenomination(int denomination, int quantity) {
        if (quantity <= 0) {
            denominations.remove(denomination);
        } else {
            denominations.put(denomination, quantity);
        }
    }

    public int getDenomination(int denomination) {
        return denominations.getOrDefault(denomination, 0);
    }

    public void clearDenominations() {
        denominations.clear();
    }
}


package io.petaleconomy.economy;

import java.util.UUID;

public class PetalAccount {
    private final UUID accountID;
    private int balance;

    public PetalAccount() {
        this(UUID.randomUUID(), 0);
    }

    public PetalAccount (UUID accountID) {
        this(accountID, 0);
    }

    public PetalAccount(UUID accountID, int balance) {
        this.accountID = accountID;
        this.balance = balance;
    }

    public UUID getAccountID() {
        return accountID;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot deposit a negative amount.");
        }

        balance += amount;
    }

    public boolean withdraw(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot withdraw a negative amount");
        }

        if (amount > balance) {
            return false;
        }

        balance -= amount;
        return true;
    }
}

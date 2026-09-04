package io.petaleconomy.economy;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PetalAccount {
    private final UUID accountID;
    private final UUID owner;
    private Set<UUID> authorizedUsers = new HashSet<>();
    private String name;
    private int balance;

    public PetalAccount(UUID owner) {
        this(UUID.randomUUID(), owner, 0);
    }

    public PetalAccount(UUID accountID, UUID owner, int balance) {
        this.accountID = accountID;
        this.owner = owner;
        this.balance = balance;

        this.name = "Primary";
        this.authorizedUsers.add(owner);
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getAccountID() {
        return accountID;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int value) {
        balance = value;
    }

    public String getAccountName() {
        return name;
    }

    //should only be used by petalAccountManager or PetalEconomyCommands
    public Set<UUID> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAccountName(String newName) {
        this.name = newName;
    }

    public boolean hasAccess(UUID playerUUID) {
        return authorizedUsers.contains(playerUUID);
    }

    public void addAuthorizedUser(UUID playerUUID) {
            authorizedUsers.add(playerUUID);
    }

    public boolean removeAuthorizedUser(UUID playerUUID) {
        if (authorizedUsers.contains(playerUUID)) {
            authorizedUsers.remove(playerUUID);
            return true;
        }

        return false;
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

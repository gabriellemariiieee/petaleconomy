package io.blossombree.petaleconomy.economy;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PetalAccount {
    private final UUID accountId;
    private final UUID owner;
    private Set<UUID> authorizedUsers = new HashSet<>();
    private String name;
    private int balance;

    public PetalAccount(UUID owner) {
        this(UUID.randomUUID(), owner, "Primary" ,0);
    }

    public PetalAccount(UUID owner, String name) {
        this(UUID.randomUUID(), owner, name, 0);
    }

    public PetalAccount(UUID accountId, UUID owner, String name, int balance) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = balance;
        this.name = name;
        this.authorizedUsers.add(owner);
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public int getBalance() {
        return balance;
    }

    public String getAccountName() {
        return name;
    }

    //should only be used by PetalAccountManager or PetalEconomyCommands
    public Set<UUID> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setBalance(int value) {
        this.balance = value;
    }

    public void setAccountName(String name) {
        this.name = name;
    }

    public boolean hasAccess(UUID playerUUID) {
        return authorizedUsers.contains(playerUUID);
    }

    public void addAuthorizedUser(UUID playerUUID) {
        authorizedUsers.add(playerUUID);
    }

    public void removeAuthorizedUser(UUID playerUUID) {
        authorizedUsers.remove(playerUUID);
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }
}

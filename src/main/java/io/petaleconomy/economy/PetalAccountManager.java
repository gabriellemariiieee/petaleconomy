package io.petaleconomy.economy;

import io.petaleconomy.capabilities.PetalCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetalAccountManager extends SavedData {
    private final Map<UUID, PetalAccount> accounts = new HashMap<>();

    public PetalAccountManager() {

    }

    public static PetalAccountManager load (CompoundTag tag) {
        PetalAccountManager manager = new PetalAccountManager();

        ListTag accountList = tag.getList("Accounts", Tag.TAG_COMPOUND);

        for (Tag accountTag : accountList) {
            CompoundTag accountData = (CompoundTag) accountTag;

            UUID accountId = accountData.getUUID("AccountId");
            UUID accountOwner = accountData.getUUID("Owner");
            int balance = accountData.getInt("Balance");

            PetalAccount account = new PetalAccount(accountId, accountOwner, balance);

            ListTag authorizedUsers = accountData.getList("Authorized Users", Tag.TAG_COMPOUND);
            for (Tag authorizedTag : authorizedUsers) {
                CompoundTag authorizedData = (CompoundTag) authorizedTag;
                UUID authorizedUser = authorizedData.getUUID("UUID");
                account.addAuthorizedUser(authorizedUser);
            }

            String accountName = accountData.getString("Name");
            account.setAccountName(accountName);

            manager.accounts.put(accountId, account);
        }

        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag accountList = new ListTag();

        for (PetalAccount account : accounts.values()) {
            CompoundTag accountData = new CompoundTag();

            accountData.putUUID("AccountId", account.getAccountID());
            accountData.putUUID("Owner", account.getOwner());
            accountData.putInt("Balance", account.getBalance());
            accountData.putString("Name", account.getAccountName());

            ListTag authorizedUsers = new ListTag();
            for (UUID playerUUID : account.getAuthorizedUsers()) {
                CompoundTag userData = new CompoundTag();
                userData.putUUID("UUID", playerUUID);
                authorizedUsers.add(userData);
            }

            accountData.put("Authorized Users", authorizedUsers);

            accountList.add(accountData);
        }

        tag.put("Accounts", accountList);

        return tag;
    }

    public PetalAccount createAccount(UUID ownerUUID) {
        PetalAccount account = new PetalAccount(ownerUUID);

        accounts.put(account.getAccountID(), account);

        setDirty();

        return account;
    }

    public PetalAccount getAccount(UUID accountId) {
        return accounts.get(accountId);
    }

    public boolean hasAccount(UUID accountId) {
        return accounts.containsKey(accountId);
    }

    public static PetalAccountManager get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

        return overworld.getDataStorage().computeIfAbsent(
                PetalAccountManager::load,
                PetalAccountManager::new,
                "petal_accounts");
    }

    public PetalAccount getAccountForPlayer(ServerPlayer player) {
        PetalBalance petalBalance = player.getCapability(PetalCapabilities.PETAL_BALANCE).orElseThrow(() ->
                new IllegalStateException("Petal Balance capability not found"));

        UUID acountId = petalBalance.getAccountId();

        if (acountId == null) {
            return null;
        }

        return getAccount(acountId);
    }

    public void setAccountName(UUID acccountId, String newName) {
        PetalAccount account = getAccount(acccountId);
        account.setAccountName(newName);

        setDirty();
    }

    public boolean addAuthorizedUser(UUID accountId, UUID playerUUID) {
        PetalAccount account = getAccount(accountId);

        if(account == null) {
            return false;
        }

        account.addAuthorizedUser(playerUUID);

        setDirty();
        return true;
    }

    public boolean removeAuthorizedUser(UUID accountId, UUID playerUUID) {
        PetalAccount account = getAccount(accountId);

        if(account == null) {
            return false;
        }

        account.removeAuthorizedUser(playerUUID);

        setDirty();
        return true;
    }

    public boolean isAccountOwner(UUID accountID, UUID playerUUID) {
        PetalAccount account = getAccount(accountID);

        if (account.getOwner() == playerUUID) {
            return true;
        }

        return false;
    }

    public boolean isAuthorizedUser(UUID accountID, UUID playerUUID) {
        PetalAccount account = getAccount(accountID);

        if (account.hasAccess(playerUUID)) {
            return true;
        }

        return false;
    }

    public void deposit(UUID accountId, int amount) {
        PetalAccount account = getAccount(accountId);

        if (account == null) {
            return;
        }

        account.deposit(amount);
        setDirty();
    }

    public boolean withdraw(UUID accountId, int amount) {
        PetalAccount account = getAccount(accountId);

        if (account == null) {
            return false;
        }

        if (!account.withdraw(amount)) {
            return false;
        }

        setDirty();
        return true;
    }

    public void setBalance(UUID accountId, int amount) {
        PetalAccount account = getAccount(accountId);

        if (account == null) {
            return;
        }

        account.setBalance(amount);
        setDirty();
    }
}

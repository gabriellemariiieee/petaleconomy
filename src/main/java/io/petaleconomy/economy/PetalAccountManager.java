package io.petaleconomy.economy;

import io.petaleconomy.capabilities.PetalCapabilities;
import io.petaleconomy.item.ModItems;
import io.petaleconomy.util.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetalAccountManager extends SavedData {
    private final Map<UUID, PetalAccount> accounts = new HashMap<>();

    public PetalAccountManager() {

    }

    //account creation
    public PetalAccount createAccount(UUID ownerUUID) {
        PetalAccount account = new PetalAccount(ownerUUID);

        accounts.put(account.getAccountID(), account);

        setDirty();

        return account;
    }

    //account retrieval
    public PetalAccount getAccount(UUID accountId) {
        return accounts.get(accountId);
    }

    public boolean hasAccount(UUID accountId) {
        return accounts.containsKey(accountId);
    }

    public PetalAccount getUsableAccount(ServerPlayer player) {
        //checks for usable Card
        for (ItemStack item : player.getInventory().items) {
            if (getAccountForCard(item, player) != null) {
                    return getAccountForCard(item, player);
            }
        }

        //checks for Primary Account
        PrimaryPetalAccount primaryAccount = player.getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT).orElseThrow(() ->
                new IllegalStateException("Primary Card not found"));

        if(primaryAccount.hasPrimaryAccount()){
            return getAccount(primaryAccount.getAccountId());
        };

        return null;
    }

    public PetalAccount getAccountForCard(ItemStack card, ServerPlayer player) {
        if (!card.is(ModItems.PETAL_CARD.get())) {
            return null;
        }

        if (!isCardValid(card, player)) {
            return null;
        }

        return getAccount(ModItems.PETAL_CARD.get().getAccountId(card));
    }

    //account modification
    public void setAccountName(UUID acccountId, String newName) {
        PetalAccount account = getAccount(acccountId);
        account.setAccountName(newName);

        setDirty();
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

    //access management
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

    //card management
    public boolean isCardValid(ItemStack stack, ServerPlayer player) {
        UUID accountId = ModItems.PETAL_CARD.get().getAccountId(stack);

        if (accountId == null || !hasAccount(accountId)) {
            return false;
        }

        PetalAccount account = getAccount(accountId);

        if (account == null || !hasAccount(accountId)) {
            return false;
        }

        return isAuthorizedUser(accountId, player.getUUID());
    }

    public boolean canCreateAccountFromCard(ItemStack card, ServerPlayer player) {
        UUID boundPlayer = ModItems.PETAL_CARD.get().getBoundPlayerUUID(card);
        //if there's a bound player and that player isnt current player
        if (boundPlayer != null && !boundPlayer.equals(player.getUUID())) {
            return false;
        }

        UUID accountId = ModItems.PETAL_CARD.get().getAccountId(card);
        //if there's an account on card and it's not deleted, if the player has access, cant override it
        if (accountId != null && hasAccount(accountId)) {
            PetalAccount account = getAccount(accountId);

            if (account.hasAccess(player.getUUID())) {
                return false;
            }
        }

        return true;
    }

    //persistence
    public static PetalAccountManager load (CompoundTag tag) {
        PetalAccountManager manager = new PetalAccountManager();

        ListTag accountList = tag.getList("Accounts", Tag.TAG_COMPOUND);

        for (Tag accountTag : accountList) {
            CompoundTag accountData = (CompoundTag) accountTag;

            UUID accountId = accountData.getUUID(Constants.ACCOUNT_ID);
            UUID accountOwner = accountData.getUUID(Constants.ACCOUNT_OWNER);
            int balance = accountData.getInt(Constants.ACCOUNT_BALANCE);

            PetalAccount account = new PetalAccount(accountId, accountOwner, balance);

            ListTag authorizedUsers = accountData.getList("Authorized Users", Tag.TAG_COMPOUND);
            for (Tag authorizedTag : authorizedUsers) {
                CompoundTag authorizedData = (CompoundTag) authorizedTag;
                UUID authorizedUser = authorizedData.getUUID(Constants.PLAYERUUID);
                account.addAuthorizedUser(authorizedUser);
            }

            String accountName = accountData.getString(Constants.ACCOUNT_NAME);
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

            accountData.putUUID(Constants.ACCOUNT_ID, account.getAccountID());
            accountData.putUUID(Constants.ACCOUNT_OWNER, account.getOwner());
            accountData.putInt(Constants.ACCOUNT_BALANCE, account.getBalance());
            accountData.putString(Constants.ACCOUNT_NAME, account.getAccountName());

            ListTag authorizedUsers = new ListTag();
            for (UUID playerUUID : account.getAuthorizedUsers()) {
                CompoundTag userData = new CompoundTag();
                userData.putUUID(Constants.PLAYERUUID, playerUUID);
                authorizedUsers.add(userData);
            }

            accountData.put("Authorized Users", authorizedUsers);

            accountList.add(accountData);
        }

        tag.put("Accounts", accountList);

        return tag;
    }

    public static PetalAccountManager get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

        return overworld.getDataStorage().computeIfAbsent(
                PetalAccountManager::load,
                PetalAccountManager::new,
                "petal_accounts");
    }
}

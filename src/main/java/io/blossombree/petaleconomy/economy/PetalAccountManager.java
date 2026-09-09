package io.blossombree.petaleconomy.economy;

import io.blossombree.petaleconomy.capabilities.PetalCapabilities;
import io.blossombree.petaleconomy.item.ModItems;
import io.blossombree.petaleconomy.item.PetalCard;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class PetalAccountManager extends SavedData {
    private final Map<UUID, PetalAccount> accounts = new HashMap<>();


    //account life
    public PetalAccount createAccount(UUID ownerUUID) {
        PetalAccount account = new PetalAccount(ownerUUID);

        accounts.put(account.getAccountId(), account);

        setDirty();

        return account;
    }

    public boolean deleteAccount(UUID accountId) {
        if (hasAccount(accountId)) {
            accounts.remove(getAccount(accountId));
            setDirty();

            return true;
        }

        return false;
    }

    //account retrieval
    public PetalAccount getAccount(UUID accountId) {
        return accounts.get(accountId);
    }

    public boolean hasAccount(UUID accountId) {
        return accounts.containsKey(accountId);
    }

    public PetalAccount getUseableAccount(ServerPlayer player) {
        //checks for useable card
        for (ItemStack slot : getPlayerCards(player)) {
            if (getAccountForCard(slot, player) != null) {
                if (isCardValid(slot, player)) {
                    return getAccountForCard(slot, player);
                }
            }
        }

        //checks for primary account
        PrimaryPetalAccount primaryPetalAccount = player.getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT).orElseThrow(() -> new IllegalStateException("No account found."));
        if (primaryPetalAccount.hasPrimaryAccount()) {
            return getAccount(primaryPetalAccount.getAccountId());
        }

        return null;
    }

    public PetalAccount getAccountForCard(ItemStack card, ServerPlayer player) {
        if (!card.is(ModItems.PETAL_CARD.get())) {
            return null;
        }

        return getAccount(ModItems.PETAL_CARD.get().getAccountId(card));
    }

    //account modification
    public void setAccountName(UUID accountId, String name) {
        PetalAccount account = getAccount(accountId);

        account.setAccountName(name);

        setDirty();
    }

    public void deposit(UUID accountId, int amount) {
        PetalAccount account = getAccount(accountId);

        if (account == null || amount < 0) {
            return;
        }

        account.deposit(amount);
        setDirty();
    }

    public boolean withdraw(UUID accountId, int amount) {
        PetalAccount account = getAccount(accountId);

        if (account == null) {
            throw new IllegalStateException("Account not found.");
        } else if (amount < 0) {
            throw new IllegalStateException("Cant withdraw a negative ammount");
        }

        if (amount > account.getBalance()) {
            return false;
        }

        account.withdraw(amount);
        setDirty();
        return true;
    }

    public void setBalance(UUID acountId, int amount) {
        PetalAccount account = getAccount(acountId);

        if (account == null) {
            return;
        }

        account.setBalance(amount);
        setDirty();
    }

    //acount info
    public String getAccountName(UUID accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account id invalid");
        }

        if (getAccount(accountId) == null) {
            throw new IllegalStateException("Account doesnt exist");
        }
        return getAccountName(accountId);
    }

    public int getBalance(UUID accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account id invalid");
        }

        if (getAccount(accountId) == null) {
            throw new IllegalStateException("Account doesnt exist");
        }
        return getBalance(accountId);
    }

    public List<UUID> getAuthorizedUsers(UUID accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account id invalid");
        }

        if (getAccount(accountId) == null) {
            throw new IllegalStateException("Account doesnt exist");
        }
        return getAuthorizedUsers(accountId);
    }

    //access management
    public boolean addAuthroizedUser(UUID accountId, UUID playerUUID) {
        PetalAccount account = getAccount(accountId);

        if (account == null) {
            return false;
        }
        if (account.hasAccess(playerUUID)) {
            return false;
        }
        account.addAuthorizedUser(playerUUID);
        setDirty();

        return true;
    }

    public boolean removeAuthorizedUser(UUID accountId, UUID playerUUID) {
        PetalAccount account = getAccount(accountId);

        if (account == null) {
            return false;
        }

        if (!account.hasAccess(playerUUID)) {
            return false;
        }

        account.removeAuthorizedUser(playerUUID);
        setDirty();

        return true;
    }

    public boolean isAccountOwner(UUID accountId, UUID playerUUID) {
        PetalAccount account = getAccount(accountId);

        if (account.getOwner() == playerUUID) {
            return true;
        }

        return false;
    }

    public boolean isAuthorizedUser(UUID accountId, UUID playerUUID) {
        PetalAccount account = getAccount(accountId);

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

    public boolean validateCard(ItemStack card) {
        PetalCard petalCard = ModItems.PETAL_CARD.get();
        UUID accountId = ModItems.PETAL_CARD.get().getAccountId(card);

        //not bound to account
        if (accountId == null) {
            petalCard.setVoid(card, false);
            return true;
        }

        PetalAccount account = getAccount(accountId);

        //has accountId but account is deleted
        if (hasAccount(accountId)) {
            petalCard.setVoid(card, true);
            return false;
        }

        UUID playerUUID = petalCard.getBoundPlayer(card);
        //player is not authorized to given account
        if (playerUUID != null && !isAuthorizedUser(accountId, playerUUID)) {
            petalCard.setVoid(card, true);
            return false;
        }
        //card is good!
        petalCard.setVoid(card, false);
        return true;
    }

    public boolean canCreateAccountFromCard(ItemStack card, ServerPlayer player) {
        UUID boundPlayer = ModItems.PETAL_CARD.get().getBoundPlayer(card);
        //if there's a bound player and that player isn't current player
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

    private List<ItemStack> getPlayerCards(ServerPlayer player) {
        List<ItemStack> cards = new ArrayList<>();

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.PETAL_CARD.get())) {
                cards.add(stack);
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ModItems.PETAL_CARD.get())) {
                cards.add(stack);
            }
        }

        //check for curious inventory in future
        return cards;
    }

    //persistence
    public static PetalAccountManager load(CompoundTag tag) {
        PetalAccountManager manager = new PetalAccountManager();

        ListTag accountList = tag.getList("Accounts", Tag.TAG_COMPOUND);

        for (Tag accountTag : accountList) {
            CompoundTag accountData = (CompoundTag) accountTag;

            UUID accountId = accountData.getUUID(Constants.ACCOUNT_ID);
            UUID accountOwner = accountData.getUUID(Constants.ACCOUNT_OWNER);
            int balance = accountData.getInt(Constants.ACCOUNT_BALANCE);
            String accountName = accountData.getString(Constants.ACCOUNT_NAME);

            PetalAccount account = new PetalAccount(accountId, accountOwner, accountName, balance);

            ListTag authorizedUsers = accountData.getList("Authorized Users", Tag.TAG_COMPOUND);
            for (Tag authorizedTag : authorizedUsers) {
                CompoundTag authorizedData = (CompoundTag) authorizedTag;
                UUID authorizedUser = authorizedData.getUUID(Constants.PLAYERUUID);
                account.addAuthorizedUser(authorizedUser);
            }

            manager.accounts.put(accountId, account);
        }

        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag accountList = new ListTag();

        for (PetalAccount account : accounts.values()) {
            CompoundTag accountData = new CompoundTag();

            accountData.putUUID(Constants.ACCOUNT_ID, account.getAccountId());
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

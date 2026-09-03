package io.petaleconomy.item;

import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.util.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class PetalCard extends Item {

    public PetalCard(Properties properties) {
        super(properties);
    }

    public UUID getAccountId(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.hasUUID(Constants.ACCOUNT_ID)) {
            return tag.getUUID(Constants.ACCOUNT_ID);
        }
        return null;
    }

    public void setAccountId(ItemStack stack, UUID accountId) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID(Constants.ACCOUNT_ID, accountId);
    }

    public boolean hasAccount(ItemStack stack) {
        return getAccountId(stack) != null;
    }

    public UUID getBoundPlayerUUID(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.hasUUID(Constants.BOUND_PLAYER_UUID)) {
            return tag.getUUID(Constants.BOUND_PLAYER_UUID);
        }
        return null;
    }

    public void setBoundPlayerUUID(ItemStack stack, UUID playerUUID) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID(Constants.BOUND_PLAYER_UUID, playerUUID);
    }

    public boolean isBound(ItemStack stack) {
        return getBoundPlayerUUID(stack) != null;
    }

    public boolean isBoundTo(ItemStack stack, UUID playerUUID) {
        return getBoundPlayerUUID(stack) == playerUUID;
    }

    public boolean isValid(ItemStack stack, ServerPlayer player) {
        UUID accountId = getAccountId(stack);

        if (accountId == null) {
            return false;
        }

        PetalAccount account = PetalAccountManager.get(player.serverLevel()).getAccount(accountId);

        if (account == null) {
            return false;
        }

        return account.hasAccess(player.getUUID());
    }

    public int getFlowerColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains(Constants.FLOWER_COLOR)) {
            return tag.getInt(Constants.FLOWER_COLOR);
        }
        return 0xab8bc4;
    }

    public void setFlowerColor(ItemStack stack, int color) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(Constants.FLOWER_COLOR, color);
    }

}

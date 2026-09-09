package io.blossombree.petaleconomy.item;

import io.blossombree.petaleconomy.economy.PetalAccount;
import io.blossombree.petaleconomy.economy.PetalAccountManager;
import io.blossombree.petaleconomy.util.Constants;
import io.blossombree.petaleconomy.util.MethodUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PetalCard extends Item {

    PetalCard(Properties properties) {
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

    public UUID getBoundPlayer(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.hasUUID(Constants.BOUND_PLAYER_UUID)) {
            return tag.getUUID(Constants.BOUND_PLAYER_UUID);
        }

        return null;
    }

    public void setBoundPlayer(ItemStack stack, UUID playerUUID) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID(Constants.BOUND_PLAYER_UUID, playerUUID);
        String name = MethodUtils.getPlayerName(getBoundPlayer(stack));
        tag.putString("PlayerName", name);
    }

    public boolean isBound(ItemStack stack) {
        return getBoundPlayer(stack) != null;
    }

    public boolean isBoundTo(ItemStack stack, UUID playerUUID) {
        return getBoundPlayer(stack) == playerUUID;
    }

    public boolean isVoid(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Voided")) {
            return tag.getBoolean("Voided");
        }

        return false;
    }

    public void setVoid (ItemStack stack, boolean voided) {
        if (voided) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putBoolean("Voided", true);
        }
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

    public DyeColor getBaseColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if(tag != null && tag.contains(Constants.BASE_COLOR)) {
             return DyeColor.byName(tag.getString(Constants.BASE_COLOR), DyeColor.WHITE);
        }

        return null;
    }

    public void setBaseColor(ItemStack stack, DyeColor baseColor) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(Constants.BASE_COLOR, baseColor.getSerializedName());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        DyeColor baseColor = getBaseColor(stack);
        String cardName = baseColor == null ? "Petal Card" : baseColor.getName().substring(0,1).toUpperCase() + baseColor.getName().substring(1) + " Petal Card";
        stack.setHoverName(Component.literal(cardName).withStyle(style -> style.withItalic(false)));

        if (isVoid(stack)) {
            tooltip.add(Component.literal("VOID").withStyle(ChatFormatting.DARK_RED));
        }

        PetalAccountManager manager = PetalAccountManager.get((ServerLevel) level);
        String accountName = manager.getAccount(getAccountId(stack)).getAccountName();
        tooltip.add(Component.literal("Account: " + accountName));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PlayerName")) {
            String playerName = tag.getString("PlayerName");

            if (playerName != null) {
                tooltip.add(Component.literal("Player: " + playerName));
            }
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}

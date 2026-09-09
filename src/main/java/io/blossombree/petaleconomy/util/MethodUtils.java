package io.blossombree.petaleconomy.util;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.dto.PlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class MethodUtils {
    public static boolean isPetalBill(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_BILLS);
    }

    public static boolean isPetalCard(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_CARDS);
    }

    public static String getPlayerName(UUID playerUUID) {
        Minecraft minecraft = Minecraft.getInstance();

        if (playerUUID != null && Minecraft.getInstance().getConnection() != null) {
            if (minecraft.getConnection().getPlayerInfo(playerUUID) != null) {
                GameProfile profile = minecraft.getConnection().getPlayerInfo(playerUUID).getProfile();
                if (profile != null) {
                    return profile.getName();
                }
            }
        }

        return null;
    }
}

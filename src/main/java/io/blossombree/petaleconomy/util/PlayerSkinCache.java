package io.blossombree.petaleconomy.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkinCache {
    private static final Map<UUID, ResourceLocation> SKINS = new HashMap<>();

    public static ResourceLocation getSkin(UUID playerUUID, String gamertag) {
        ResourceLocation cachedSkin = SKINS.get(playerUUID);

        if (cachedSkin != null) {
            return cachedSkin;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.getConnection() != null) {
            PlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(playerUUID);
            if (playerInfo != null) {
                cachePlayer(playerInfo);

                return playerInfo.getSkinLocation();
            }
        }

        GameProfile profile = new GameProfile(playerUUID, gamertag);

        ResourceLocation cachedMinecraftSkin = minecraft.getSkinManager().getInsecureSkinLocation(profile);

        if (cachedMinecraftSkin != null) {
            SKINS.put(playerUUID, cachedMinecraftSkin);
            return cachedMinecraftSkin;
        }

        return DefaultPlayerSkin.getDefaultSkin(playerUUID);
    }

    public static void cachePlayer(PlayerInfo playerInfo) {
        Minecraft minecraft = Minecraft.getInstance();

        GameProfile profile = playerInfo.getProfile();

        minecraft.getSkinManager().registerSkins(profile, (type, location, profileTexture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                SKINS.put(profile.getId(), location);
            }
        }, false);

    }
}

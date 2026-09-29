package io.blossombree.petaleconomy.gui.components;

import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.util.Constants;
import io.blossombree.petaleconomy.util.MethodUtils;
import io.blossombree.petaleconomy.util.PlayerSkinCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PlayerEntry extends PetalButton {
    private final UUID playerId;
    public static final int HEIGHT = 10;

    public PlayerEntry(int x, int y, UUID playerId, OnPress onPress) {
        super(x, y, 76, 10, Component.empty(), Constants.PLAYER_ENTRY_BUTTON_NORMAL, Constants.PLAYER_ENTRY_BUTTON_SELECTED, onPress);

        this.playerId = playerId;
    }

    public UUID getPlayerUUID() {
        return this.playerId;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        String gamertag = MethodUtils.getPlayerName(playerId);
        ResourceLocation skin = PlayerSkinCache.getSkin(playerId, gamertag);

        guiGraphics.blit(skin, getX() + 1, getY() + 1, 8, 8, 8, 8, 8, 64, 64);
        guiGraphics.blit(skin, getX() + 1, getY() + 1, 8, 8, 40, 8, 8, 8, 64, 64);
        guiGraphics.drawString(Minecraft.getInstance().font, gamertag, getX() + 11, getY() + 1, 0xFFFFFF);
    }
}

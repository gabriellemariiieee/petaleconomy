package io.blossombree.petaleconomy.gui;

import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PetalButton extends Button {
    private final ResourceLocation normalTexture;
    private final ResourceLocation selectedTexture;
    private float textScale = 1.0f;

    public PetalButton(int x, int y, int width, int height, Component message, ResourceLocation normalTexture, ResourceLocation selectedTexture, OnPress onPress) {
        super (x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.normalTexture = normalTexture;
        this.selectedTexture = selectedTexture;
    }

    public PetalButton setTextScale(float scale) {
        this.textScale = scale;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = isHoveredOrFocused() ? selectedTexture : normalTexture;

        guiGraphics.blit(texture, getX(), getY(), 0, 0, width, height, width, height);
        if (!getMessage().getString().isEmpty()) {
            float scale = textScale;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(getX() + width / 2.0F, getY() + height / 2.0F, 0);
            guiGraphics.pose().scale(scale, scale, 1.0F);

            guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), 0, -4, 0xFFFFFF);

            guiGraphics.pose().popPose();
        }
    }
}


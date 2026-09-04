package io.petaleconomy.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractPetalScreen<T extends AbstractPetalMenu> extends AbstractContainerScreen<T> {
    protected static final int TEXT_COLOR = 0x404040;
    protected ResourceLocation texture;

    protected AbstractPetalScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = 10000;
        this.imageHeight = 186;
        this.texture = texture;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        pGuiGraphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    protected void drawCenteredText(GuiGraphics pGuiGraphics, String pText, int pCenterX, int pY) {
        pGuiGraphics.drawString(this.font, pText, leftPos + pCenterX, topPos + pY, TEXT_COLOR, false);
    }

    protected void renderAccountNameLabel(GuiGraphics guiGraphics, int centX, int y) {
        drawCenteredText(guiGraphics, "Name:", centX, y);
    }

    protected void renderAccountBalanceLabel(GuiGraphics guiGraphics, int centX, int y) {
        drawCenteredText(guiGraphics, "Balance:", centX, y);
    }

    protected void renderAccountName(GuiGraphics guiGraphics, int centX, int y) {
        drawCenteredText(guiGraphics, menu.getCurrentAccountName(), centX, y);
    }

    protected void renderBalance (GuiGraphics guiGraphics, int centerX, int y) {
        drawCenteredText(guiGraphics, "✿" + String.valueOf(menu.getCurrentBalance()), centerX, y);
    }

}

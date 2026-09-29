package io.blossombree.petaleconomy.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.gui.menus.AbstractPetalMenu;
import io.blossombree.petaleconomy.network.BackButtonPacket;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AbstractPetalScreen<T extends AbstractPetalMenu> extends AbstractContainerScreen<T> {
    protected static final int TEXT_COLOR = 0x404040;
    protected ResourceLocation texture;
    protected PetalButton backButton;

    @Override
    protected void init() {
        super.init();
        backButton = new PetalButton(leftPos + 7, topPos + 5, 14, 14, Component.empty(), Constants.BACK_BUTTON_NORMAL, Constants.BACK_BUTTON_SELECTED, button -> {
            PetalNetwork.CHANNEL.sendToServer(new BackButtonPacket());
        });
    }

    protected AbstractPetalScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture) {
        super (menu, playerInventory, title);
        this.inventoryLabelY = 10000;
        this.titleLabelX = 25;
        this.titleLabelY = 8;
        this.imageHeight = 186;
        this.texture = texture;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        guiGraphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    protected void drawCenteredText(GuiGraphics guiGraphics, String text, int centX, int y) {
        guiGraphics.drawString(this.font, text, leftPos + centX, topPos + y, TEXT_COLOR, false);
    }

    protected void renderAccountNameLabel(GuiGraphics guiGraphics, int centX, int y) {
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.account_name").getString(), centX, y);
    }

    protected void renderAccountBalanceLabel(GuiGraphics guiGraphics, int centX, int y) {
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.balance").getString(), centX, y);
    }

    protected void renderAccountName(GuiGraphics guiGraphics, int centerX, int y) {
        drawCenteredText(guiGraphics, menu.getCurrentAccountName(), centerX, y);
    }

    protected void renderBalance (GuiGraphics guiGraphics, int centerX, int y) {
        drawCenteredText(guiGraphics, "✿" + String.valueOf(menu.getCurrentBalance()), centerX, y);
    }

    protected void renderTransactionAmount(GuiGraphics guiGraphics, int centerX, int y) {
        drawCenteredText(guiGraphics, "✿" + String.valueOf(menu.getTransactionAmount()), centerX, y);
    }

    protected void renderPendingBalance(GuiGraphics guiGraphics, int centerX, int y) {
        drawCenteredText(guiGraphics, "✿" + String.valueOf(menu.getTransactionAmount()), centerX, y);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getFocused() instanceof EditBox editBox) {
            if (keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

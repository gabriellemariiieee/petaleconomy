package io.petaleconomy.gui;

import io.petaleconomy.PetalEconomy;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class APDMainScreen extends AbstractPetalScreen<APDMainMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/containers/automatic_petal_dispenser.png");

    public APDMainScreen(APDMainMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super (pMenu, pPlayerInventory, pTitle, TEXTURE);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);

        renderAccountNameLabel(guiGraphics, 7, 22);
        renderAccountName(guiGraphics, 35, 22);
        renderAccountBalanceLabel(guiGraphics, 7, 32);
        renderBalance(guiGraphics, 51, 32);
    }
}

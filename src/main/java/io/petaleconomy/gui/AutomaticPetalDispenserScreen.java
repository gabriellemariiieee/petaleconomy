package io.petaleconomy.gui;

import io.petaleconomy.PetalEconomy;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AutomaticPetalDispenserScreen extends AbstractContainerScreen<AutomaticPetalDispenserMenu> {

    //private static final ResourceLocation TEXTURE = new ResourceLocation(PetalEconomy.MODID, "textures/gui/automatic_petal_dispenser_gui.png");

    public AutomaticPetalDispenserScreen(AutomaticPetalDispenserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphiocs, float pPartialTick, int pMouseX, int pMouseY) {

    }
}

package io.blossombree.petaleconomy.gui.screens;

import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/* public class PortableAPDScreen extends AbstractPetalScreen<>{
    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new PetalButton(leftPos + 37, topPos + 63, 42, 14, Component.translatable("gui.petal_economy.deposit"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
        addRenderableWidget(new PetalButton(leftPos + 97, topPos + 63, 42, 14, Component.translatable("gui.petal_economy.withdraw"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
    }

    public PortableAPDScreen(menu, Inventory inventory, Component title) {
        super(menu, inventory, title, Constants.PORTABLE_APD_MAIN_MENU);
        this.titleLabelX = 7;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.name").getString(), 7, 21);
        renderAccountName(guiGraphics, 36, 21);
        renderAccountBalanceLabel(guiGraphics, 7, 31);
        renderBalance(guiGraphics, 51, 31);
    }
} */

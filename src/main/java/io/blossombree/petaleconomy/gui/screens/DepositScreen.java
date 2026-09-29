package io.blossombree.petaleconomy.gui.screens;

import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.gui.menus.DepositMenu;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DepositScreen extends AbstractPetalScreen<DepositMenu>{
    @Override
    protected void init() {
        addRenderableWidget(new PetalButton(leftPos + 127, topPos + 82, 42, 14, Component.translatable("gui.petal_economy.deposit"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
        addRenderableWidget(backButton);
    }

    public DepositScreen(DepositMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable("gui.petal_economy.deposit"), Constants.DEPOSIT_MENU);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        //drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.name").getString(), 7, 22);
        //renderAccountName(guiGraphics, 36, 21);
        //renderAccountBalanceLabel(guiGraphics, 7, 33);
        //renderBalance(guiGraphics, 52, 33);
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.pending").getString(), 7, 75);
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.pending_balance").getString(), 7, 86);
        renderTransactionAmount(guiGraphics, 50, 75);
        renderPendingBalance(guiGraphics, 72, 86);
    }
}

package io.blossombree.petaleconomy.gui.screens;

import io.blossombree.petaleconomy.gui.MinusButton;
import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.gui.PlusButton;
import io.blossombree.petaleconomy.gui.menus.WithdrawMenu;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WithdrawScreen extends AbstractPetalScreen<WithdrawMenu> {
    @Override
    protected void init() {
        addRenderableWidget(new PetalButton(leftPos + 127, topPos + 82, 42, 14, Component.translatable("gui.petal_economy.deposit"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
        addRenderableWidget(backButton);
        addRenderableWidget(new PlusButton(8, 40, 1, menu));
        addRenderableWidget(new PlusButton(44, 40, 5, menu));
        addRenderableWidget(new PlusButton(80, 40, 10, menu));
        addRenderableWidget(new PlusButton(116, 40, 20, menu));

        addRenderableWidget(new MinusButton(17, 40, 1, menu));
        addRenderableWidget(new MinusButton(53, 40, 5, menu));
        addRenderableWidget(new MinusButton(89, 40, 10, menu));
        addRenderableWidget(new MinusButton(125, 40, 20, menu));

        addRenderableWidget(new PlusButton(8, 70, 50, menu));
        addRenderableWidget(new PlusButton(44, 70, 100, menu));
        addRenderableWidget(new PlusButton(80, 70, 1000, menu));
        addRenderableWidget(new PlusButton(116, 70, 5000, menu));
        addRenderableWidget(new PlusButton(152, 70, 10000, menu));

        addRenderableWidget(new MinusButton(17, 70, 50, menu));
        addRenderableWidget(new MinusButton(53, 70, 100, menu));
        addRenderableWidget(new MinusButton(89, 70, 1000, menu));
        addRenderableWidget(new MinusButton(125, 70, 5000, menu));
        addRenderableWidget(new MinusButton(161, 70, 10000, menu));
    }

    public WithdrawScreen(WithdrawMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable("gui.petal_economy.withdraw"), Constants.WITHDRAW_MENU);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.pending").getString(), 7, 82);
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.pending_balance").getString(), 7, 93);
        renderTransactionAmount(guiGraphics, 50, 82);
        renderPendingBalance(guiGraphics, 72, 93);
    }
}

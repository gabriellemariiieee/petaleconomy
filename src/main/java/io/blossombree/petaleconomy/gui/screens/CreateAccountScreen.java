package io.blossombree.petaleconomy.gui.screens;

import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.gui.menus.CreateAccountMenu;
import io.blossombree.petaleconomy.network.CreateAccountPacket;
import io.blossombree.petaleconomy.network.EjectDepositPacket;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CreateAccountScreen extends AbstractPetalScreen<CreateAccountMenu> {

    private EditBox accountNameBox;

    @Override
    protected void init() {
        super.init();

        accountNameBox = new EditBox(font, leftPos + 79, topPos + 20, 84, 9, Component.translatable("gui.petal_economy.edit_name"));
        accountNameBox.setMaxLength(15);
        accountNameBox.setHint(Component.translatable("gui.petal_economy.edit_name"));

        addRenderableWidget(accountNameBox);
        addRenderableWidget(new PetalButton(leftPos + 66, topPos + 82, 42, 14, Component.translatable("gui.petal_economy.create"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            PetalNetwork.CHANNEL.sendToServer(new CreateAccountPacket(accountNameBox.getValue()));
        }));
        addRenderableWidget(new PetalButton(leftPos + 127, topPos + 53, 42, 14, Component.translatable("gui.petal_economy.eject"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            PetalNetwork.CHANNEL.sendToServer(new EjectDepositPacket());
        }));

        if (menu.isFromMainMenu()) {
            addRenderableWidget(backButton);
        }
    }

    public CreateAccountScreen(CreateAccountMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable("gui.petal_economy.create_account"), Constants.CREATE_ACCOUNT_MENU);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        renderAccountNameLabel(guiGraphics, 7, 21);
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.bind_card").getString(), 7, 40);
        renderAccountBalanceLabel(guiGraphics, 79, 40);
        renderPendingBalance(guiGraphics, 101, 56);
    }
}

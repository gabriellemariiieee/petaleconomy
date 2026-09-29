package io.blossombree.petaleconomy.gui.screens;

import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.network.DeleteAccountPacket;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DeleteConfirmationScreen extends Screen {
    private String accountName;
    private EditBox editBox;

    public DeleteConfirmationScreen(String accountName) {
        super(Component.translatable("gui.petal_economy.delete_confirmation"));

        this.accountName = accountName;
    }

    @Override
    protected void init() {
        super.init();

        editBox = new EditBox(font, leftPos() - 42, topPos(), 84, 9, Component.translatable("gui.petal_economy.edit_name"));

        addRenderableWidget(editBox);
        addRenderableWidget(new PetalButton(leftPos() - 21, topPos() + 20, 42, 14, Component.translatable("gui.petal_economy.confirm"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> confirm()));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(font, title, leftPos(), topPos() - 40, 0xFFFFFF);
        guiGraphics.drawCenteredString(font, Component.literal(Component.translatable("gui.petal_economy.enter_name").getString() + accountName), leftPos(), topPos() - 15, 0xFFFFFF);
    }

    private int topPos() {
        return (height) / 2;
    }

    private int leftPos() {
        return (width) / 2;
    }

    private void confirm() {
        PetalNetwork.CHANNEL.sendToServer(new DeleteAccountPacket(editBox.getValue()));
    }
}

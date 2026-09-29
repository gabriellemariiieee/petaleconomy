package io.blossombree.petaleconomy.gui.screens;

import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.gui.menus.ManageAccountMenu;
import io.blossombree.petaleconomy.item.ModItems;
import io.blossombree.petaleconomy.network.ManageAccountPacket;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ManageAccountScreen extends AbstractPetalScreen<ManageAccountMenu> {
    private EditBox accountNameBox;
    private PetalButton deleteButton;
    private PetalButton doneButton;
    private ColorButton colorButton;
    private String accountName = "";
    private int flowerColor;
    private int newColor;
    private ItemStack card;

    @Override
    protected void init() {
        super.init();

        accountNameBox = new EditBox(font, leftPos + 7, topPos + 30, 84, 9, Component.translatable("gui.petal_economy.edit_name"));
        accountNameBox.setMaxLength(15);
        accountNameBox.setValue(menu.getCurrentAccountName());

        colorButton = new ColorButton(leftPos + 161, topPos + 62, 7, 7, flowerColor, button -> openColorPicker());
        deleteButton = new PetalButton(leftPos + 7, topPos + 82, 42, 14, Component.translatable("gui.petal_economy.delete"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> openDeleteConfirmation());
        doneButton = new PetalButton(leftPos + 66, topPos + 82, 42, 14, Component.translatable("gui.petal_economy.done"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> { PetalNetwork.CHANNEL.sendToServer(new ManageAccountPacket(accountNameBox.getValue(), newColor)); });

        addRenderableWidget(accountNameBox);
        addRenderableWidget(deleteButton);
        addRenderableWidget(colorButton);
        addRenderableWidget(doneButton);
        addRenderableWidget(backButton);
    }

    public ManageAccountScreen(ManageAccountMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable("gui.petal_economy.manage_account"), Constants.MANAGE_ACCOUNT_MENU);
        card = menu.getCurrentCard(1);
        if (card.isEmpty()) {
            card = menu.getCurrentCard(0);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        renderAccountNameLabel(guiGraphics, 7, 21);
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.bind_card").getString(), 7, 57);
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.customize_card").getString(), 95, 52);
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.flower_color").getString(), 95, 62);
    }

    private void openColorPicker() {
        accountName = accountNameBox.getValue();

        minecraft.setScreen(new ColorPickerScreen(this, flowerColor, color -> {
            newColor = color;
            colorButton.setColor(color);
        }));
    }

    private void openDeleteConfirmation() {
        minecraft.setScreen(new DeleteConfirmationScreen(menu.getCurrentAccountName()));
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (accountNameBox != null) {
            accountNameBox.setEditable(menu.isAccountOwner());
        }

        if (deleteButton != null) {
            deleteButton.visible = menu.isAccountOwner();
            if (deleteButton.visible) {
                doneButton.setX(leftPos + 127);
            }
        }

        if (!card.isEmpty()) {
            flowerColor = ModItems.PETAL_CARD.get().getFlowerColor(card);
        } else {
            flowerColor = 0xFFFFFFFF;
        }

        if (colorButton.color != newColor || colorButton.color != flowerColor) {
            colorButton.color = flowerColor;
        }
    }

    private class ColorButton extends Button {
        private int color;

        public ColorButton(int x, int y, int width, int height, int color, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.color = color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int borderColor = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF000000;

            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, color | 0xFF000000);
            guiGraphics.renderOutline(getX(), getY(), width, height, borderColor);
        }
    }
}

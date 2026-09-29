package io.blossombree.petaleconomy.gui.screens;

import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.gui.menus.APDMainMenu;
import io.blossombree.petaleconomy.network.OpenCreateMenuPacket;
import io.blossombree.petaleconomy.network.OpenManageAccessPacket;
import io.blossombree.petaleconomy.network.OpenManageMenuPacket;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class APDMainScreen extends  AbstractPetalScreen<APDMainMenu> {
    private PetalButton manageAccess;

    @Override
    protected void init() {
        super.init();

        manageAccess = new PetalButton(leftPos + 97, topPos + 63, 42, 14, Component.translatable("gui.petal_economy.access"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            PetalNetwork.CHANNEL.sendToServer(new OpenManageAccessPacket(menu.getBlockPos()));
        });

        addRenderableWidget(new PetalButton(leftPos + 37, topPos + 44, 42, 14, Component.translatable("gui.petal_economy.deposit"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
        addRenderableWidget(new PetalButton(leftPos + 97, topPos + 44, 42, 14, Component.translatable("gui.petal_economy.withdraw"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}).setTextScale(0.85F));
        addRenderableWidget(new PetalButton(leftPos + 37, topPos + 63, 42, 14, Component.translatable("gui.petal_economy.manage"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            PetalNetwork.CHANNEL.sendToServer(new OpenManageMenuPacket(menu.getBlockPos()));
        }));
        addRenderableWidget(manageAccess);
        addRenderableWidget(new PetalButton(leftPos + 66, topPos + 82, 42, 14, Component.translatable("gui.petal_economy.create"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            PetalNetwork.CHANNEL.sendToServer(new OpenCreateMenuPacket(menu.getBlockPos(), true));
        }));
    }

    public APDMainScreen(APDMainMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, Constants.APD_MAIN_MENU);
        this.titleLabelX = 7;
        this.titleLabelY = 6;
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (manageAccess != null) {
          manageAccess.active = menu.isAccountOwner();
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.name").getString(), 7, 21);
        renderAccountName(guiGraphics, 36, 21);
        renderAccountBalanceLabel(guiGraphics, 7, 31);
        renderBalance(guiGraphics, 51, 31);
    }
}

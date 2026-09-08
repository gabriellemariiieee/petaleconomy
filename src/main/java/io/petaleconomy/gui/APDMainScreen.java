package io.petaleconomy.gui;

import io.petaleconomy.PetalEconomy;
import io.petaleconomy.network.OpenCreateMenuPacket;
import io.petaleconomy.network.PetalNetwork;
import io.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class APDMainScreen extends AbstractPetalScreen<APDMainMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/containers/automatic_petal_dispenser.png");

    @Override
    protected void init() {
        super.init();

        //addRenderableWidget(new PetalButton(leftPos + 37, topPos + 44, 42, 14, Component.literal("Deposit"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
        //addRenderableWidget(new PetalButton(leftPos + 97, topPos + 44, 42, 14, Component.literal("Withdraw"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
        //addRenderableWidget(new PetalButton(leftPos + 37, topPos + 63, 42, 14, Component.literal("Manage"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
        //addRenderableWidget(new PetalButton(leftPos + 97, topPos + 63, 42, 14, Component.literal("Access"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {}));
        addRenderableWidget(new PetalButton(leftPos + 67, topPos + 62, 42, 14, Component.literal("Create"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            PetalNetwork.CHANNEL.sendToServer(new OpenCreateMenuPacket());
        }));
    }

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

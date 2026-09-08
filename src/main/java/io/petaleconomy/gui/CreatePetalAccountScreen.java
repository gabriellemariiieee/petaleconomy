package io.petaleconomy.gui;

import io.petaleconomy.PetalEconomy;
import io.petaleconomy.network.CreateAccountPacket;
import io.petaleconomy.network.EjectDepositPacket;
import io.petaleconomy.network.PetalNetwork;
import io.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CreatePetalAccountScreen extends AbstractPetalScreen<CreatePetalAccountMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/containers/create_petal_account.png");

    private EditBox accountNameBox;
    private PetalButton createButton;

    @Override
    protected void init() {
        super.init();

        accountNameBox = new EditBox(
                font, leftPos + 79, topPos + 20, 84, 9, Component.literal("Name...")
        );
        accountNameBox.setMaxLength(15);

        addRenderableWidget(accountNameBox);
        addRenderableWidget(new PetalButton(leftPos + 127, topPos + 82, 42, 14, Component.literal("Create"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            new CreateAccountPacket(accountNameBox.getValue());
        }));
        addRenderableWidget(new PetalButton(leftPos + 127, topPos + 53, 42, 14, Component.literal("Eject"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            PetalNetwork.CHANNEL.sendToServer(new EjectDepositPacket());
        }));
    }

    public CreatePetalAccountScreen(CreatePetalAccountMenu pMenu, Inventory pInv, Component pTitle) {
        super(pMenu, pInv, Component.literal("Create Account"), TEXTURE);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);

        renderAccountNameLabel(guiGraphics, 7, 21);
        drawCenteredText(guiGraphics, "Bind Card: ", 7, 40);
        renderAccountBalanceLabel(guiGraphics, 79, 40);
        renderBalance(guiGraphics, 101, 56);
    }
}

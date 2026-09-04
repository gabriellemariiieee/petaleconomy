package io.petaleconomy.gui;

import io.petaleconomy.PetalEconomy;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CreatePetalAccountScreen extends AbstractPetalScreen<CreatePetalAccountMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/containers/create_petal_account");
    private EditBox accountNameBox;

    @Override
    protected void init() {
        super.init();

        accountNameBox = new EditBox(
                font, leftPos + 79, topPos + 20, 84, 9, Component.literal("Name...")
        );
        accountNameBox.setMaxLength(15);

        addRenderableWidget(accountNameBox);
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

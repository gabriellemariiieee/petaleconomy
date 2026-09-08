package io.petaleconomy.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PetalButton extends Button {

    private final ResourceLocation normalTexture;
    private final ResourceLocation selectedTexture;

    public PetalButton(int x, int y, int width, int height, Component message, ResourceLocation normalTexture, ResourceLocation selectedTexture, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.normalTexture = normalTexture;
        this.selectedTexture = selectedTexture;
    }
}

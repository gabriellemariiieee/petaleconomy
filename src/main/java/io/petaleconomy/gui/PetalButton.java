package io.petaleconomy.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class PetalButton extends Button {

    protected PetalButton(int p_259075_, int p_259271_, int p_260232_, int p_260028_, Component p_259351_, OnPress p_260152_, CreateNarration p_259552_) {
        super(p_259075_, p_259271_, p_260232_, p_260028_, p_259351_, p_260152_, p_259552_);
    }

    protected PetalButton(Builder builder) {
        super(builder);
    }
}

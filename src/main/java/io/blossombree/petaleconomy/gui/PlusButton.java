package io.blossombree.petaleconomy.gui;

import io.blossombree.petaleconomy.gui.menus.WithdrawMenu;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.network.chat.Component;

public class PlusButton extends PetalButton {
    private final int denomination;
    private final WithdrawMenu menu;

    public PlusButton(int x, int y, int denomination, WithdrawMenu menu) {
        super(x, y, 7, 7, Component.empty(), Constants.PLUS_BUTTON_NORMAL, Constants.PLUS_BUTTON_SELECTED, button -> {});
        this.denomination = denomination;
        this.menu = menu;
    }

    @Override
    public void onPress() {
        menu.addDenomination(denomination);
    }
}

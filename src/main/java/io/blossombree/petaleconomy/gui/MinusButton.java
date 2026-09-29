package io.blossombree.petaleconomy.gui;

import io.blossombree.petaleconomy.gui.menus.WithdrawMenu;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.network.chat.Component;

public class MinusButton extends PetalButton{
    private final int denomination;
    private final WithdrawMenu menu;

    public MinusButton(int x, int y, int denomination, WithdrawMenu menu) {
        super(x, y, 7, 7, Component.empty(), Constants.MINUS_BUTTON_NORMAL, Constants.MINUS_BUTTON_SELECTED, button -> {});
        this.denomination = denomination;
        this.menu = menu;
    }

    @Override
    public void onPress() {
        menu.subtractDenomination(denomination);
    }
}

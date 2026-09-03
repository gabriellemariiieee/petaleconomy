package io.petaleconomy.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class Constants {
    //static strings
    public static final String ACCOUNT_ID = "AccountId";
    public static final String ACCOUNT_OWNER = "Owner";
    public static final String BOUND_PLAYER_UUID = "BoundPlayerUUID";
    public static final String FLOWER_COLOR = "FlowerColor";
    public static final String ACCOUNT_BALANCE = "Balance";
    public static final String ACCOUNT_NAME = "Name";
    public static final String PLAYERUUID = "UUID";

    //ints
    public static final int CARD_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int ONE_BILL_SLOT = 2;
    public static final int TEN_THOUSAND_BILL_SLOT = 10;
    public static final int[] DENOMINATIONS = {
            1, 5, 10, 20, 50, 100, 500, 1000, 10000
    };
    public static final int CARD_SLOT_X = 8;
    public static final int CARD_SLOT_Y = 17;
}

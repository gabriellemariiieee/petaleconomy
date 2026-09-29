package io.blossombree.petaleconomy.util;

import io.blossombree.petaleconomy.PetalEconomy;
import net.minecraft.resources.ResourceLocation;

public final class Constants {
    //static strings
    public static final String ACCOUNT_ID = "AccountId";
    public static final String ACCOUNT_OWNER = "Owner";
    public static final String BOUND_PLAYER_UUID = "BoundPlayerUUID";
    public static final String FLOWER_COLOR = "FlowerColor";
    public static final String BASE_COLOR = "BaseColor";
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
    public static final int CARD_SLOT_X = 152;
    public static final int CARD_SLOT_Y = 22;

    public static final int HOTBAR_SLOT_COUNT = 9;
    public static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    public static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    public static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    public static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    public static final int VANILLA_FIRST_SLOT_INDEX = 0;
    public static final int BE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    public static final int BE_INVENTORY_SLOT_COUNT = 11;

    //textures
    public static final ResourceLocation MENU_BUTTON_NORMAL = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/menu_button.png");
    public static final ResourceLocation MENU_BUTTON_SELECTED = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/menu_button_selected.png");
    public static final ResourceLocation BACK_BUTTON_NORMAL = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/back_button.png");
    public static final ResourceLocation BACK_BUTTON_SELECTED = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/back_button_selected.png");
    public static final ResourceLocation PLUS_BUTTON_NORMAL = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/plus.png");
    public static final ResourceLocation PLUS_BUTTON_SELECTED = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/plus_selected.png");
    public static final ResourceLocation MINUS_BUTTON_NORMAL = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/minus.png");
    public static final ResourceLocation MINUS_BUTTON_SELECTED = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/minus_selected.png");
    public static final ResourceLocation PLAYER_ENTRY_BUTTON_NORMAL = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/player_entry.png");
    public static final ResourceLocation PLAYER_ENTRY_BUTTON_SELECTED = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/buttons/player_entry_selected.png");
    public static final ResourceLocation APD_MAIN_MENU = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/containers/automatic_petal_dispenser.png");
    public static final ResourceLocation PORTABLE_APD_MAIN_MENU = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/containers/portable_apd.png");
    public static final ResourceLocation CREATE_ACCOUNT_MENU = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/create_petal_account.png");
    public static final ResourceLocation MANAGE_ACCESS_MENU = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/manage_account_access.png");
    public static final ResourceLocation MANAGE_ACCOUNT_MENU = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/manage_petal_account.png");
    public static final ResourceLocation DEPOSIT_MENU = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/petal_bank_deposit.png");
    public static final ResourceLocation WITHDRAW_MENU = ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "textures/gui/petal_bank_withdraw.png");
}

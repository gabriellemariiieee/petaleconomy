package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.PetalEconomy;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, PetalEconomy.MODID);

    public static final RegistryObject<MenuType<CreateAccountMenu>> CREATE_ACCOUNT_MENU = registerMenuType("create_account_menu", CreateAccountMenu::new);
    public static final RegistryObject<MenuType<APDMainMenu>> APD_MAIN_MENU = registerMenuType("apd_main_menu", APDMainMenu::new);
    public static final RegistryObject<MenuType<ManageAccountMenu>> MANAGE_ACCOUNT_MENU = registerMenuType("manage_account_menu", ManageAccountMenu::new);
    public static final RegistryObject<MenuType<ManageAccessMenu>> MANAGE_ACCESS_MENU = registerMenuType("manage_access_menu", ManageAccessMenu::new);
    public static final RegistryObject<MenuType<DepositMenu>> DEPOSIT_MENU = registerMenuType("deposit_menu", DepositMenu::new);
    public static final RegistryObject<MenuType<WithdrawMenu>> WITHDRAW_MENU = registerMenuType("withdraw_menu", WithdrawMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType (String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

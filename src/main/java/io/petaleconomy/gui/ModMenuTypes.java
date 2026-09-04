package io.petaleconomy.gui;

import io.petaleconomy.PetalEconomy;
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

    public static final RegistryObject<MenuType<APDMainMenu>> APD_MAIN_MENU =
            registerMenuType("apd_main_menu", APDMainMenu::new);
    public static final RegistryObject<MenuType<CreatePetalAccountMenu>> CREATE_PETAL_ACCOUNT_MENU =
            registerMenuType("create_petal_account_menu", CreatePetalAccountMenu::new);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

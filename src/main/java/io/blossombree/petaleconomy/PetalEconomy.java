package io.blossombree.petaleconomy;

import com.mojang.logging.LogUtils;
import io.blossombree.petaleconomy.block.ModBlocks;
import io.blossombree.petaleconomy.block.entities.ModBlockEntities;
import io.blossombree.petaleconomy.events.PetalCapabilitiesEvents;
import io.blossombree.petaleconomy.gui.menus.ModMenuTypes;
import io.blossombree.petaleconomy.gui.screens.APDMainScreen;
import io.blossombree.petaleconomy.gui.screens.CreateAccountScreen;
import io.blossombree.petaleconomy.gui.screens.ManageAccessScreen;
import io.blossombree.petaleconomy.gui.screens.ManageAccountScreen;
import io.blossombree.petaleconomy.item.ModCreativeModTabs;
import io.blossombree.petaleconomy.item.ModItems;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.recipe.ModRecipeSerializers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(PetalEconomy.MODID)
public class PetalEconomy {
    public static final String MODID = "petaleconomy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public PetalEconomy(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(PetalCapabilitiesEvents.class);
        PetalNetwork.register();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    { }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ModMenuTypes.CREATE_ACCOUNT_MENU.get(), CreateAccountScreen::new);
            MenuScreens.register(ModMenuTypes.APD_MAIN_MENU.get(), APDMainScreen::new);
            MenuScreens.register(ModMenuTypes.MANAGE_ACCOUNT_MENU.get(), ManageAccountScreen::new);
            MenuScreens.register(ModMenuTypes.MANAGE_ACCESS_MENU.get(), ManageAccessScreen::new);
            ItemProperties.register(
                    ModItems.PETAL_CARD.get(),
                    ResourceLocation.fromNamespaceAndPath(
                            PetalEconomy.MODID,
                            "base_color"
                    ),
                    (stack, level, entity, seed) -> {
                        DyeColor dyeColor = ModItems.PETAL_CARD.get().getBaseColor(stack);

                        if (dyeColor == null) {
                            return 0.0F;
                        }

                        if (dyeColor == DyeColor.WHITE) {
                            return 0.0001F;
                        }

                        return dyeColor.getId() / 15.0F;
                    });
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(
                    (stack, tintIndex) -> {
                        if (tintIndex == 1) {
                            int color = ModItems.PETAL_CARD.get().getFlowerColor(stack);
                            ModItems.PETAL_CARD.get().setFlowerColor(stack, color);
                            return color;
                        }
                        return 0xFFFFFF;
                    },
                    ModItems.PETAL_CARD.get()
            );
        }
    }
}

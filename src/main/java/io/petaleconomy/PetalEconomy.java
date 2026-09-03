package io.petaleconomy;

import com.mojang.logging.LogUtils;
import io.petaleconomy.block.ModBlocks;
import io.petaleconomy.block.entities.ModBlockEntities;
import io.petaleconomy.commands.PetalEconomyCommands;
import io.petaleconomy.events.PetalCapabilitiesEvents;
import io.petaleconomy.gui.ModMenuTypes;
import io.petaleconomy.item.ModCreativeModTabs;
import io.petaleconomy.item.ModItems;
import io.petaleconomy.item.PetalCard;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
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
        ModMenuTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(PetalCapabilitiesEvents.class);
        MinecraftForge.EVENT_BUS.register(PetalEconomyCommands.class);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener((this::addCreative));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //MenuScreens.register(ModMenuTypes.AUTOMATIC_PETAL_DISPENSER_MENU.get(), AutomaticPetalDispenserDepositScreen::new);
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(
                    (stack, tintIndex) -> {
                        if (tintIndex == 1) {
                            return ModItems.PETAL_CARD.get().getFlowerColor(stack);
                        }

                        return 0xFFFFFF;
                    },
                    ModItems.PETAL_CARD.get()
            );
        }
    }
}
package io.blossombree.petaleconomy.item;

import io.blossombree.petaleconomy.PetalEconomy;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PetalEconomy.MODID);

    public static final RegistryObject<CreativeModeTab> PETAL_ECONOMY_TAB = CREATIVE_MODE_TABS.register("petal_economy_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TEN_THOUSAND_PETALS_BILL.get()))
                    .title(Component.translatable("creativetab.petal_economy_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.ONE_PETAL_BILL.get());
                        pOutput.accept(ModItems.FIVE_PETALS_BILL.get());
                        pOutput.accept(ModItems.TEN_PETALS_BILL.get());
                        pOutput.accept(ModItems.TWENTY_PETALS_BILL.get());
                        pOutput.accept(ModItems.FIFTY_PETALS_BILL.get());
                        pOutput.accept(ModItems.ONE_HUNDRED_PETALS_BILL.get());
                        pOutput.accept(ModItems.FIVE_HUNDRED_PETALS_BILL.get());
                        pOutput.accept(ModItems.ONE_THOUSAND_PETALS_BILL.get());
                        pOutput.accept(ModItems.TEN_THOUSAND_PETALS_BILL.get());
                        pOutput.accept(ModItems.PETAL_CARD.get());
                        //pOutput.accept(ModBlocks.AUTOMATIC_PETAL_DISPENSER.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

package io.petaleconomy.item;

import io.petaleconomy.PetalEconomy;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PetalEconomy.MODID);

    public static final RegistryObject<Item> ONE_PETAL_BILL = ITEMS.register("one_petal_bill", () -> new PetalBill(1));
    public static final RegistryObject<Item> FIVE_PETALS_BILL = ITEMS.register("five_petals_bill", () -> new PetalBill(5));
    public static final RegistryObject<Item> TEN_PETALS_BILL = ITEMS.register("ten_petals_bill", () -> new PetalBill(10));
    public static final RegistryObject<Item> TWENTY_PETALS_BILL = ITEMS.register("twenty_petals_bill", () -> new PetalBill(20));
    public static final RegistryObject<Item> FIFTY_PETALS_BILL = ITEMS.register("fifty_petals_bill", () -> new PetalBill(50));
    public static final RegistryObject<Item> ONE_HUNDRED_PETALS_BILL = ITEMS.register("one_hundred_petals_bill", () -> new PetalBill(100));
    public static final RegistryObject<Item> FIVE_HUNDRED_PETALS_BILL = ITEMS.register("five_hundred_petals_bill", () -> new PetalBill(500));
    public static final RegistryObject<Item> ONE_THOUSAND_PETALS_BILL = ITEMS.register("one_thousand_petals_bill", () -> new PetalBill(1000));
    public static final RegistryObject<Item> TEN_THOUSAND_PETALS_BILL = ITEMS.register("ten_thousand_petals_bill", () -> new PetalBill(10000));
    public static final RegistryObject<PetalCard> PETAL_CARD = ITEMS.register("petal_card", () -> new PetalCard(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

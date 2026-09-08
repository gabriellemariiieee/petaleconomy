package io.blossombree.petaleconomy.util;

import io.blossombree.petaleconomy.PetalEconomy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> PETAL_BILLS = tag("petal_bill");
        public static final TagKey<Item> PETAL_CARDS = tag("petal_card");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, name));
        }
    }
}

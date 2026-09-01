package io.petaleconomy.util;

import io.petaleconomy.PetalEconomy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    /*public static class Blocks {
        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(PetalEconomy.MODID, name));
        }
    }*/

    public static class Items {
        public static final TagKey<Item> PETAL_BILLS = tag("petal_bills");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, name));
        }
    }
}

package io.petaleconomy.util;

import net.minecraft.world.item.ItemStack;

public class PetalItemUtils {
    public static boolean isPetalBill(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_BILLS);
    }

    public static boolean isPetalCard(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_CARDS);
    }

}

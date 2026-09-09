package io.blossombree.petaleconomy.recipe;

import io.blossombree.petaleconomy.item.ModItems;
import io.blossombree.petaleconomy.item.PetalCard;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PetalCardDyeRecipe extends CustomRecipe {

    public PetalCardDyeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack card = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if(stack.isEmpty()) {
                continue;
            }

            if (stack.is(ModItems.PETAL_CARD.get())) {
                if (!card.isEmpty()) {
                    return false;
                }
                card = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                if (!dye.isEmpty()) {
                    return false;
                }

                dye = stack;
            } else {
                return false;
            }
        }

        return !card.isEmpty() && !dye.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack card = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.is(ModItems.PETAL_CARD.get())) {
                card = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                dye = stack;
            }
        }

        if (card.isEmpty() || dye.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = card.copy();
        DyeColor color = ((DyeItem) dye.getItem()).getDyeColor();

        ModItems.PETAL_CARD.get().setBaseColor(result, color);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PETAL_CARD_DYE.get();
    }
}

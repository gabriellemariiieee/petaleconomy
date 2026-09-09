package io.blossombree.petaleconomy.recipe;

import io.blossombree.petaleconomy.PetalEconomy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, PetalEconomy.MODID);

    public static final Supplier<RecipeSerializer<PetalCardDyeRecipe>> PETAL_CARD_DYE = RECIPE_SERIALIZERS.register("petal_card_dye", () -> new SimpleCraftingRecipeSerializer<>(PetalCardDyeRecipe::new));

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}

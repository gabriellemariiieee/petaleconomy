package io.blossombree.petaleconomy.block.entities;

import io.blossombree.petaleconomy.PetalEconomy;
import io.blossombree.petaleconomy.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PetalEconomy.MODID);

    public static final RegistryObject<BlockEntityType<APDBlockEntity>> APD = BLOCK_ENTITIES.register("apd_be", () ->
            BlockEntityType.Builder.of(APDBlockEntity::new, ModBlocks.AUTOMATIC_PETAL_DISPENSER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

package io.petaleconomy.item.handler;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class PortableAPDItemHandlerProvider implements ICapabilityProvider {
    private final LazyOptional<IItemHandler> handler;

    public PortableAPDItemHandlerProvider(ItemStack stack) {
        this.handler = LazyOptional.of(() -> new PortableAPDItemHandler(stack));
    }

    @Override
    public <T> @Nullable LazyOptional<T> getCapability(
            Capability<T> capability,
            @Nullable Direction side
            ) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return handler.cast();
        }

        return LazyOptional.empty();
    }
}

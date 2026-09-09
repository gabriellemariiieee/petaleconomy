package io.blossombree.petaleconomy.events;

import io.blossombree.petaleconomy.PetalEconomy;
import io.blossombree.petaleconomy.capabilities.PetalCapabilities;
import io.blossombree.petaleconomy.economy.PrimaryAccountProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PetalCapabilitiesEvents {

    @SubscribeEvent
    public static void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> entityEvent) {
        if (entityEvent.getObject() instanceof Player) {
            entityEvent.addCapability(ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "primary_account"), new PrimaryAccountProvider());
        }
    }

    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> itemEvent) {

    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT)
                .ifPresent(oldAccount -> {
                    event.getEntity().getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT).ifPresent(newPrimaryAccount -> {
                        newPrimaryAccount.setAccountId(oldAccount.getAccountId());
                    });
                });

        event.getOriginal().invalidateCaps();
    }
}

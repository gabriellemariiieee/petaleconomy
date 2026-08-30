package io.petaleconomy.events;

import io.petaleconomy.PetalEconomy;
import io.petaleconomy.balance.PetalBalanceProvider;
import io.petaleconomy.capabilities.PetalCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PetalCapabilitiesEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "petal_balance"), new PetalBalanceProvider());
        }
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(PetalCapabilities.PETAL_BALANCE)
                .ifPresent(oldBalance -> {
                    event.getEntity().getCapability(PetalCapabilities.PETAL_BALANCE).ifPresent(newBalance -> {
                        newBalance.setBalance(oldBalance.getBalance());
                    });
                });

        event.getOriginal().invalidateCaps();
    }
}

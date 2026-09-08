package io.petaleconomy.events;

import io.petaleconomy.PetalEconomy;
import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.economy.PrimaryPetalAccount;
import io.petaleconomy.capabilities.PetalCapabilities;
import io.petaleconomy.economy.PrimaryAccountProvider;
import io.petaleconomy.item.ModItems;
import io.petaleconomy.item.handler.PortableAPDItemHandlerProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PetalCapabilitiesEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event, AttachCapabilitiesEvent<ItemStack> itemEvent) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "primary_account"), new PrimaryAccountProvider());
        }

        if (itemEvent.getObject().is(ModItems.PORTABLE_APD.get())) {
            itemEvent.addCapability(ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "portable_apd_inventory"), new PortableAPDItemHandlerProvider(itemEvent.getObject()));
        }

    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT)
                .ifPresent(oldBalance -> {
                    event.getEntity().getCapability(PetalCapabilities.PRIMARY_PETAL_ACCOUNT).ifPresent(newPrimaryAccount -> {
                        newPrimaryAccount.setAccountId(oldBalance.getAccountId());
                    });
                });

        event.getOriginal().invalidateCaps();
    }
}

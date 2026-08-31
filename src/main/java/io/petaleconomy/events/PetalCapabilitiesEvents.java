package io.petaleconomy.events;

import io.petaleconomy.PetalEconomy;
import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.economy.PetalBalance;
import io.petaleconomy.economy.PetalBalanceProvider;
import io.petaleconomy.capabilities.PetalCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

    //temp
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PetalBalance petalBalance = player.getCapability(
                PetalCapabilities.PETAL_BALANCE
        ).orElseThrow(() ->
                new IllegalStateException("Petal Balance capability not found")
        );

        if (petalBalance.getAccountId() == null) {
            PetalAccount account = PetalAccountManager.get(player.serverLevel()).createAccount();

            petalBalance.setAccountId(account.getAccountID());
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
                        newBalance.setAccountId(oldBalance.getAccountId());
                    });
                });

        event.getOriginal().invalidateCaps();
    }
}

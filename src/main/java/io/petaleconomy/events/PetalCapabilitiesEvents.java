package io.petaleconomy.events;

import io.petaleconomy.PetalEconomy;
import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.economy.PrimaryPetalAccount;
import io.petaleconomy.capabilities.PetalCapabilities;
import io.petaleconomy.economy.PrimaryAccountProvider;
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
            event.addCapability(ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "primary_account"), new PrimaryAccountProvider());
        }
    }

    //temp
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PrimaryPetalAccount primaryAccount = player.getCapability(
                PetalCapabilities.PRIMARY_PETAL_ACCOUNT
        ).orElseThrow(() ->
                new IllegalStateException("Primary account capability not found")
        );

        if (primaryAccount.getAccountId() == null) {
            PetalAccount account = PetalAccountManager.get(player.serverLevel()).createAccount(player.getUUID());

            primaryAccount.setAccountId(account.getAccountID());
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

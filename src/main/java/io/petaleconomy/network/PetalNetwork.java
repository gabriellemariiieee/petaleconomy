package io.petaleconomy.network;

import io.petaleconomy.PetalEconomy;
import io.petaleconomy.gui.AbstractPetalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PetalNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(PetalEconomy.MODID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, SyncAccountNamePacket.class, SyncAccountNamePacket::encode, SyncAccountNamePacket::new, (packet, context) -> {
            context.get().enqueueWork(() -> {
                if (context.get().getDirection().getReceptionSide().isClient()) {
                    Minecraft minecraft = Minecraft.getInstance();

                    if (minecraft.player != null && minecraft.player.containerMenu instanceof AbstractPetalMenu menu) {
                        menu.setCurrentAccountName(packet.getAccountName());
                    }
                }

                context.get().setPacketHandled(true);
            });
        });
    }
}

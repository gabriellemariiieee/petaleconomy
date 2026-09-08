package io.petaleconomy.network;

import io.petaleconomy.PetalEconomy;
import io.petaleconomy.gui.AbstractPetalMenu;
import io.petaleconomy.gui.CreatePetalAccountMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
        CHANNEL.registerMessage(packetId++, SyncAccountNamePacket.class, SyncAccountNamePacket::encode, SyncAccountNamePacket::new, SyncAccountNamePacket::handle);

        CHANNEL.registerMessage(packetId++, CreateAccountPacket.class, CreateAccountPacket::encode, CreateAccountPacket::new, CreateAccountPacket::handle);

        CHANNEL.registerMessage(packetId++, EjectDepositPacket.class, EjectDepositPacket::encode, EjectDepositPacket::decode, EjectDepositPacket::handle);

        CHANNEL.registerMessage(packetId++, OpenCreateMenuPacket.class, OpenCreateMenuPacket::encode, OpenCreateMenuPacket::decode, OpenCreateMenuPacket::handle);
    }
}

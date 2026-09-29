package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.PetalEconomy;
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

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.registerMessage(nextId(), CreateAccountPacket.class, CreateAccountPacket::encode, CreateAccountPacket::new, CreateAccountPacket::handle);
        CHANNEL.registerMessage(nextId(), EjectDepositPacket.class, EjectDepositPacket::encode, EjectDepositPacket::decode, EjectDepositPacket::handle);
        CHANNEL.registerMessage(nextId(), BackButtonPacket.class, BackButtonPacket::encode, BackButtonPacket::decode, BackButtonPacket::handle);
        CHANNEL.registerMessage(nextId(), SyncAccountNamePacket.class, SyncAccountNamePacket::encode, SyncAccountNamePacket::new, SyncAccountNamePacket::handle);
        CHANNEL.registerMessage(nextId(), OpenCreateMenuPacket.class, OpenCreateMenuPacket::encode, OpenCreateMenuPacket::new, OpenCreateMenuPacket::handle);
        CHANNEL.registerMessage(nextId(), OpenManageMenuPacket.class, OpenManageMenuPacket::encode, OpenManageMenuPacket::new, OpenManageMenuPacket::handle);
        CHANNEL.registerMessage(nextId(), ManageAccountPacket.class, ManageAccountPacket::encode, ManageAccountPacket::new, ManageAccountPacket::handle);
        CHANNEL.registerMessage(nextId(), DeleteAccountPacket.class, DeleteAccountPacket::encode, DeleteAccountPacket::new, DeleteAccountPacket::handle);
        CHANNEL.registerMessage(nextId(), GrantAccessPacket.class, GrantAccessPacket::encode, GrantAccessPacket::new, GrantAccessPacket::handle);
        CHANNEL.registerMessage(nextId(), RevokeAccessPacket.class, RevokeAccessPacket::encode, RevokeAccessPacket::new, RevokeAccessPacket::handle);
        CHANNEL.registerMessage(nextId(), OpenManageAccessPacket.class, OpenManageAccessPacket::encode, OpenManageAccessPacket::new, OpenManageAccessPacket::handle);
        CHANNEL.registerMessage(nextId(), AuthorizedUsersPacket.class, AuthorizedUsersPacket::encode, AuthorizedUsersPacket::new, AuthorizedUsersPacket::handle);
    }
}

package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.gui.menus.ManageAccessMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class RevokeAccessPacket {
    private final UUID playerUUID;

    public RevokeAccessPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public RevokeAccessPacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
    }

    public static void handle(RevokeAccessPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player != null && player.containerMenu instanceof ManageAccessMenu menu) {
                menu.removeAuthorizedUser(packet.playerUUID);
                PetalNetwork.CHANNEL.sendToServer(new AuthorizedUsersPacket(menu.getAuthorizedUsers()));
            }
        });

        context.get().setPacketHandled(true);
    }
}

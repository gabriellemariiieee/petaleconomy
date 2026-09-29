package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.gui.menus.ManageAccessMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GrantAccessPacket {
    private final UUID playerUUID;

    public GrantAccessPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public GrantAccessPacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
    }

    public static void handle(GrantAccessPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player != null && player.containerMenu instanceof ManageAccessMenu menu) {
                menu.addAuthorizedUser(packet.playerUUID);
                PetalNetwork.CHANNEL.sendToServer(new AuthorizedUsersPacket(menu.getAuthorizedUsers()));
            }
        });

        context.get().setPacketHandled(true);
    }
}

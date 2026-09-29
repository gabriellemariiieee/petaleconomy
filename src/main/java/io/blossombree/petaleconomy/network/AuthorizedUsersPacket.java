package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.gui.menus.AbstractPetalMenu;
import io.blossombree.petaleconomy.gui.menus.ManageAccessMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class AuthorizedUsersPacket {
    private final List<UUID> authorizedUsers;

    public AuthorizedUsersPacket(List<UUID> authorizedUsers) {
        this.authorizedUsers = authorizedUsers;
    }

    public AuthorizedUsersPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();

        this.authorizedUsers = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            authorizedUsers.add(buf.readUUID());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(authorizedUsers.size());

        for (UUID playerUUID : authorizedUsers) {
            buf.writeUUID(playerUUID);
        }
    }

    public static void handle(AuthorizedUsersPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide().isClient()) {
                Minecraft minecraft = Minecraft.getInstance();

                if (minecraft.player != null && minecraft.player.containerMenu instanceof ManageAccessMenu menu) {
                    menu.setAuthorizedUsers(packet.authorizedUsers);
                }
            }
        });

        context.get().setPacketHandled(true);
    }
}

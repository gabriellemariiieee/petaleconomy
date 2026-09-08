package io.petaleconomy.network;

import io.petaleconomy.gui.APDMainMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenCreateMenuPacket {
    public static void encode(OpenCreateMenuPacket packet, FriendlyByteBuf buf) {
    }

    public static OpenCreateMenuPacket decode(FriendlyByteBuf buf) {
        return new OpenCreateMenuPacket();
    }

    public static void handle(OpenCreateMenuPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null) {
                return;
            }

            if(player.containerMenu instanceof APDMainMenu menu) {
                menu.
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

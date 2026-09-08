package io.petaleconomy.network;

import io.petaleconomy.gui.CreatePetalAccountMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EjectDepositPacket {

    public static void encode(EjectDepositPacket packet, FriendlyByteBuf buf) {
    }

    public static EjectDepositPacket decode(FriendlyByteBuf buf) {
        return new EjectDepositPacket();
    }

    public static void handle(EjectDepositPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null) {
                return;
            }

            if (player.containerMenu instanceof CreatePetalAccountMenu menu) {
                menu.ejectStartingBalance();
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

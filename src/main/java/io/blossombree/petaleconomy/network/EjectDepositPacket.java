package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.gui.menus.CreateAccountMenu;
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

    public static void handle(EjectDepositPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player == null) {
                return;
            }

            if (player.containerMenu instanceof CreateAccountMenu menu) {
                menu.ejectStartingBalance();
            }
        });

        context.get().setPacketHandled(true);
    }
}

package io.petaleconomy.network;

import io.petaleconomy.gui.AbstractPetalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAccountNamePacket {
    private final String accountName;

    public SyncAccountNamePacket(String accountName) {
        this.accountName = accountName;
    }

    public SyncAccountNamePacket(FriendlyByteBuf buf) {
        this.accountName = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(accountName);
    }

    public String getAccountName() {
        return accountName;
    }

    public static void handle(SyncAccountNamePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide().isClient()) {
                Minecraft minecraft = Minecraft.getInstance();

                if (minecraft.player != null && minecraft.player.containerMenu instanceof AbstractPetalMenu menu) {
                    menu.setCurrentAccountName(packet.getAccountName());
                }
            }

            context.get().setPacketHandled(true);
        });
    }
}

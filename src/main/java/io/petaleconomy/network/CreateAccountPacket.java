package io.petaleconomy.network;

import io.petaleconomy.gui.CreatePetalAccountMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CreateAccountPacket {
    private final String accountName;

    public CreateAccountPacket(String accountName) {
        this.accountName = accountName;
    }

    public CreateAccountPacket(FriendlyByteBuf buf) {
        this.accountName = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(accountName);
    }

    public String getAccountName() {
        return accountName;
    }

    public static void handle(CreateAccountPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null) {
                return;
            }

            if (player.containerMenu instanceof CreatePetalAccountMenu menu) {
                menu.createAccount(packet.getAccountName());
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

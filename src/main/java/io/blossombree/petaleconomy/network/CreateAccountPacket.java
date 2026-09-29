package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.gui.menus.APDMainMenu;
import io.blossombree.petaleconomy.gui.menus.CreateAccountMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

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

    public static void handle(CreateAccountPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player == null) {
                return;
            }

            if (player.containerMenu instanceof CreateAccountMenu menu) {
                if (menu.createAccount(packet.getAccountName())) {
                    BlockPos pos = menu.getSourcePos();

                    if (pos == null) {
                        return;
                    }

                    BlockEntity blockEntity = player.level().getBlockEntity(pos);

                    if (!(blockEntity instanceof APDBlockEntity apd)) {
                        return;
                    }
                    apd.returnCard();
                    NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, inventory, player1) -> new APDMainMenu(containerId, inventory, apd), Component.translatable("gui.petal_economy.petal_bank")), buf -> buf.writeBlockPos(pos));
                }
            }
        });

        context.get().setPacketHandled(true);
    }
}

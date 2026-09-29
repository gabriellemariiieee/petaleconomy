package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.gui.menus.ManageAccountMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenManageMenuPacket {
    private final BlockPos blockPos;

    public OpenManageMenuPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public OpenManageMenuPacket(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public static void handle(OpenManageMenuPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player == null) {
                return;
            }

            ServerLevel level = player.serverLevel();
            if (!(level.getBlockEntity(packet.getBlockPos()) instanceof APDBlockEntity apd)) {
                return;
            }

            NetworkHooks.openScreen(player, new SimpleMenuProvider(
                            (containerId, inventory, player1) -> new ManageAccountMenu(containerId, inventory, apd),
                            Component.translatable("gui.petal_economy.manage_account")
                    ),
                    buf -> {
                        buf.writeBlockPos(packet.getBlockPos());
                    }
            );
        });

        context.get().setPacketHandled(true);
    }
}

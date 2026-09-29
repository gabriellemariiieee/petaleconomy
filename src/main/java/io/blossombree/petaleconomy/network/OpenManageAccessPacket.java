package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.gui.menus.ManageAccessMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenManageAccessPacket {
    private final BlockPos blockPos;

    public OpenManageAccessPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public OpenManageAccessPacket(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
    }

    public static void handle(OpenManageAccessPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = context.get().getSender();

            if (serverPlayer == null) {
                return;
            }

            ServerLevel level = serverPlayer.serverLevel().getLevel();
            if (!(level.getBlockEntity(packet.blockPos) instanceof APDBlockEntity apd)) {
                return;
            }

            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (containerId, inventory, player) -> new ManageAccessMenu(containerId, inventory, apd),
                    Component.translatable("gui.petal_economy.manage_access")),
                    buf -> buf.writeBlockPos(packet.blockPos)
            );
        });

        context.get().setPacketHandled(true);
    }
}

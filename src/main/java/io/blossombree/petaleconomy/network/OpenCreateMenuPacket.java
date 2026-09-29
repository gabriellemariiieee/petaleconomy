package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.gui.menus.CreateAccountMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenCreateMenuPacket {
    private final BlockPos blockPos;
    private final boolean fromMainMenu;

    public OpenCreateMenuPacket(BlockPos blockPos, boolean fromMainMenu) {
        this.blockPos = blockPos;
        this.fromMainMenu = fromMainMenu;
    }

    public OpenCreateMenuPacket(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.fromMainMenu = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeBoolean(fromMainMenu);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public boolean getFromMainMenu() {
        return fromMainMenu;
    }

    public static void handle(OpenCreateMenuPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player == null) {
                return;
            }

            ServerLevel level = player.serverLevel();
            if (!(level.getBlockEntity(packet.getBlockPos()) instanceof APDBlockEntity blockEntity)) {
                return;
            }

            blockEntity.moveCard();

            NetworkHooks.openScreen(player, new SimpleMenuProvider(
                    (containerId, inventory, player1) -> new CreateAccountMenu(containerId, inventory, blockEntity, packet.getFromMainMenu()),
                    Component.translatable("gui.petal_economy.create_account")
                    ),
                    buf -> {
                        buf.writeBlockPos(packet.getBlockPos());
                        buf.writeBoolean(packet.getFromMainMenu());
                    }
            );
        });

        context.get().setPacketHandled(true);
    }
}

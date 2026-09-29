package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.gui.menus.APDMainMenu;
import io.blossombree.petaleconomy.gui.menus.ManageAccountMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class ManageAccountPacket {
    private final String accountName;
    private final int flowerColor;

    public ManageAccountPacket(String accountName, int flowerColor) {
        this.accountName = accountName;
        this.flowerColor = flowerColor;
    }

    public ManageAccountPacket(FriendlyByteBuf buf) {
        this.accountName = buf.readUtf();
        this.flowerColor = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(accountName);
        buf.writeInt(flowerColor);
    }

    public String getAccountName() {
        return this.accountName;
    }

    public int getFlowerColor() {
        return this.flowerColor;
    }

    public static void handle(ManageAccountPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player == null) {
                return;
            }

            if (player.containerMenu instanceof ManageAccountMenu menu) {
                if (menu.manageAccount(packet.getAccountName(), packet.getFlowerColor())) {
                    BlockPos pos = menu.getSourcePos();

                    if (pos == null) {
                        return;
                    }

                    BlockEntity blockEntity = player.level().getBlockEntity(pos);

                    if (!(blockEntity instanceof APDBlockEntity apd)) {
                        return;
                    }

                    NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, inventory, player1) -> new APDMainMenu(containerId, inventory, apd), Component.translatable("gui.petal_economy.petal_bank")), buf -> buf.writeBlockPos(pos));
                }
            }
        });

        context.get().setPacketHandled(true);
    }
}

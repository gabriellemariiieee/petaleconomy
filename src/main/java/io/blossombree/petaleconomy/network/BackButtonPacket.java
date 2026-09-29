package io.blossombree.petaleconomy.network;

import io.blossombree.petaleconomy.block.entities.APDBlockEntity;
import io.blossombree.petaleconomy.gui.menus.APDMainMenu;
import io.blossombree.petaleconomy.gui.menus.AbstractPetalMenu;
import io.blossombree.petaleconomy.gui.menus.CreateAccountMenu;
import io.blossombree.petaleconomy.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class BackButtonPacket {

    public BackButtonPacket() {}

    public static void encode(BackButtonPacket packet, FriendlyByteBuf buf) {}

    public static BackButtonPacket decode(FriendlyByteBuf buf) {
        return new BackButtonPacket();
    }

    public static void handle(BackButtonPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player == null) {
                return;
            }

            if (!(player.containerMenu instanceof AbstractPetalMenu menu)) {
                return;
            }

            switch (menu.getMenuSource()) {
                case APD -> {
                    BlockPos pos = menu.getSourcePos();

                    if (pos == null) {
                        return;
                    }

                    BlockEntity blockEntity = player.level().getBlockEntity(pos);

                    if (!(blockEntity instanceof APDBlockEntity apd)) {
                        return;
                    }

                    if (menu instanceof CreateAccountMenu) {
                        apd.returnCard();
                    }

                    NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, inventory, player1) -> new APDMainMenu(containerId, inventory, apd), Component.translatable("gui.petal_economy.petal_bank")), buf -> buf.writeBlockPos(pos));
                }
                /* case PORTABLE_APD -> {
                    int slot = menu.getSourceSlot();

                    ItemStack stack = player.getInventory().getItem(slot);

                    if (!stack.is(ModItems.PORTABLE_APD.get())) {
                        return;
                    }

                    NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, inventory, player1) -> new ));
                }*/
            }
        });

        context.get().setPacketHandled(true);
    }
}

package io.petaleconomy.item;

import io.petaleconomy.capabilities.PetalCapabilities;
import io.petaleconomy.gui.AbstractPetalMenu;
import io.petaleconomy.gui.CreatePetalAccountMenu;
import io.petaleconomy.gui.PortableAPDMainMenu;
import io.petaleconomy.item.handler.PortableAPDItemHandler;
import io.petaleconomy.util.Constants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public class PortableAPD extends Item implements MenuProvider {
    private AbstractPetalMenu menu;

    public PortableAPD(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PortableAPDMainMenu(containerId, inventory, player, this);
    }


    @Override
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        if (!level.isClientSide() && player instanceof ServerPlayer) {

            NetworkHooks.openScreen(player, portableAPD, buf -> buf.);
        }
    }

}

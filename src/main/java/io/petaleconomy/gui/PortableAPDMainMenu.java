package io.petaleconomy.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PortableAPDMainMenu extends AbstractPetalMenu{

    public PortableAPDMainMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, extraData.readInt());
    }

    protected PortableAPDMainMenu(int containerId, Inventory inv, int slot) {
        super(ModMenuTypes.PORTABLE_APD_MAIN_MENU.get(), containerId, inv.player);
        ItemStack stack = inv.getItem(slot);

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create());
    }
}

package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class DepositMenu extends AbstractPetalMenu{

    public DepositMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory);
    }

    public DepositMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.DEPOSIT_MENU.get(), containerId, inventory.player);
        addPlayerInventory(inventory);
        //addCardSlot();
        addInputSlot(80, Constants.CARD_SLOT_Y);

        for (int c = 0; c < 9; c++) {
            this.addSlot(new SlotItemHandler(itemHandler, c + 2, 8 + c * 18, 52));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

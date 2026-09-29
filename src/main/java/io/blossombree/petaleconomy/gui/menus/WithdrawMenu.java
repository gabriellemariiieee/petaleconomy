package io.blossombree.petaleconomy.gui.menus;

import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class WithdrawMenu extends AbstractPetalMenu{

    public WithdrawMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory);
    }

    public WithdrawMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.WITHDRAW_MENU.get(), containerId, inventory.player);
        //addCardSlot();
        addPlayerInventory(inventory);
        for (int x = Constants.ONE_BILL_SLOT; x < Constants.TEN_THOUSAND_BILL_SLOT; x++) {
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 7; c += 2) {
                    this.addSlot(new SlotItemHandler(itemHandler, x, 8 + c * 18, r * 30 + 22));
                }
            }
        }
        this.addSlot(new SlotItemHandler(itemHandler, Constants.TEN_THOUSAND_BILL_SLOT, 153, 52));


    }

    public void addDenomination(int denomination) {

    }

    public void subtractDenomination(int denomination) {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

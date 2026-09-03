package io.petaleconomy.gui;

import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import io.petaleconomy.util.Constants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public abstract class AbstractPetalMenu extends AbstractContainerMenu {

    protected final ServerPlayer player;
    protected final IItemHandler itemHandler;

    protected AbstractPetalMenu(@Nullable MenuType<?> menuType, int containerId, ServerPlayer player, IItemHandler iItemHandler) {

        super(menuType, containerId);
        this.player = player;
        this.itemHandler = iItemHandler;
    }

    //inventory helper method
    public void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }

        //Hotbar
        for(int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 * + col * 18, 162));
        }
    }

    public void addCardSlot() {
        this.addSlot(new SlotItemHandler(itemHandler, Constants.CARD_SLOT, Constants.CARD_SLOT_X, Constants.CARD_SLOT_Y));
    }

    protected ItemStack getCurrentCard() {
        return itemHandler.getStackInSlot(Constants.CARD_SLOT);
    }

    protected PetalAccount getCurrentAccount() {
        ItemStack card = getCurrentCard();

        PetalAccountManager manager = PetalAccountManager.get(player.serverLevel());

        if(!card.isEmpty()) {
            return manager.getAccountForCard(card, player);
        }

        return manager.getUsableAccount(player);
    }
}

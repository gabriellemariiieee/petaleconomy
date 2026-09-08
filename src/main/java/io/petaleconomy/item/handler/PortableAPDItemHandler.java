package io.petaleconomy.item.handler;

import io.petaleconomy.util.Constants;
import io.petaleconomy.util.PetalItemUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class PortableAPDItemHandler extends ItemStackHandler {
    private final ItemStack stack;

    public PortableAPDItemHandler(ItemStack stack) {
        super(11);
        this.stack = stack;

        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("Inventory")) {
            deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        stack.getOrCreateTag().put("Inventory", serializeNBT());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if(slot == Constants.INPUT_SLOT) {
            return PetalItemUtils.isPetalBill(stack);
        }

        if(slot == Constants.CARD_SLOT) {
            return PetalItemUtils.isPetalCard(stack);
        }

        if (slot >= Constants.ONE_BILL_SLOT && slot <= Constants.TEN_THOUSAND_BILL_SLOT) {
            return false;
        }

        return false;
    }
}

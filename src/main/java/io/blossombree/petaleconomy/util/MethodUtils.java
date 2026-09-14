package io.blossombree.petaleconomy.util;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.dto.PlayerInfo;
import io.blossombree.petaleconomy.item.PetalBill;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.UUID;

public class MethodUtils {
    public static boolean isPetalBill(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_BILLS);
    }

    public static boolean isPetalCard(ItemStack stack) {
        return stack.is(ModTags.Items.PETAL_CARDS);
    }

    public static void sortInput(ItemStackHandler itemHandler) {
        ItemStack input = itemHandler.getStackInSlot(Constants.INPUT_SLOT);

        if (input.isEmpty() || !(input.getItem() instanceof PetalBill bill)) {
            return;
        }

        int[] denominations = Constants.DENOMINATIONS;
        int value = bill.getValue();
        for (int i = 0; i < Constants.DENOMINATIONS.length; i++) {
            if (Constants.DENOMINATIONS[i] == value) {
                int slot = i + 2;

                ItemStack existing = itemHandler.getStackInSlot(slot);
                if (existing.isEmpty()) {
                    itemHandler.setStackInSlot(slot, input.copy());
                    itemHandler.setStackInSlot(Constants.INPUT_SLOT, ItemStack.EMPTY);
                } else if (ItemStack.isSameItem(existing, input)) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    int amountToMove = Math.min(space, input.getCount());

                    existing.grow(amountToMove);
                    input.shrink(amountToMove);

                    itemHandler.setStackInSlot(slot, existing);
                    itemHandler.setStackInSlot(Constants.INPUT_SLOT, input);
                }

                break;
            }
        }
    }

    public static String getPlayerName(UUID playerUUID) {
        Minecraft minecraft = Minecraft.getInstance();

        if (playerUUID != null && Minecraft.getInstance().getConnection() != null) {
            if (minecraft.getConnection().getPlayerInfo(playerUUID) != null) {
                GameProfile profile = minecraft.getConnection().getPlayerInfo(playerUUID).getProfile();
                if (profile != null) {
                    return profile.getName();
                }
            }
        }

        return null;
    }
}

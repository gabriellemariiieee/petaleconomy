package io.petaleconomy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PetalBill extends Item {
    private final int value;

    //initialize with a value
    public PetalBill(int value) {
        super(new Item.Properties().stacksTo(64));
        this.value = value;
    }

    //shows value on hover
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pToolTipComponents, TooltipFlag pIsAdvanced) {
        pToolTipComponents.add(Component.literal("✿" + String.valueOf(value)).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pStack, pLevel, pToolTipComponents, pIsAdvanced);
    }

    public int getValue() {
        return value;
    }
}

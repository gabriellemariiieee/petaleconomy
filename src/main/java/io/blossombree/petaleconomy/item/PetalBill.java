package io.blossombree.petaleconomy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PetalBill extends Item {
    private final int value;
    public PetalBill(int value) {
        super(new Item.Properties().stacksTo(64));
        this.value = value;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> toolTipCommands, TooltipFlag isAdvanced) {
        toolTipCommands.add(Component.literal("✿" + String.valueOf(value)).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, toolTipCommands, isAdvanced);
    }

    public int getValue() {
        return value;
    }
}

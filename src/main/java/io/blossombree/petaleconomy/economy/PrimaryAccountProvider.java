package io.blossombree.petaleconomy.economy;

import io.blossombree.petaleconomy.capabilities.PetalCapabilities;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.awt.*;

public class PrimaryAccountProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final PrimaryPetalAccount primaryAccount = new PrimaryPetalAccount();

    @Override
    public <T>LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (capability == PetalCapabilities.PRIMARY_PETAL_ACCOUNT) {
            return LazyOptional.of(() -> primaryAccount).cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        if(primaryAccount.getAccountId() != null) {
            tag.putUUID(Constants.ACCOUNT_ID, primaryAccount.getAccountId());
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.hasUUID(Constants.ACCOUNT_ID)) {
            primaryAccount.setAccountId(tag.getUUID(Constants.ACCOUNT_ID));
        }
    }
}

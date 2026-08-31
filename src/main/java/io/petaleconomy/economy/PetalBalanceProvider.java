package io.petaleconomy.economy;

import io.petaleconomy.capabilities.PetalCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PetalBalanceProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final PetalBalance balance = new PetalBalance();

    @Override
    public <T>LazyOptional<T> getCapability(Capability<T> capability, Direction side) {

        if (capability == PetalCapabilities.PETAL_BALANCE) {
            return LazyOptional.of(() -> balance).cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        if (balance.getAccountId() != null) {
            tag.putUUID("AccountId", balance.getAccountId());
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.hasUUID("AccountId")) {
            balance.setAccountId(tag.getUUID("AccountId"));
        }
    }
}
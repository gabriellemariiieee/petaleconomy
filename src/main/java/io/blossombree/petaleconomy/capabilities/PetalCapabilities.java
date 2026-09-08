package io.blossombree.petaleconomy.capabilities;

import io.blossombree.petaleconomy.economy.PrimaryPetalAccount;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class PetalCapabilities {
    public static final Capability<PrimaryPetalAccount> PRIMARY_PETAL_ACCOUNT = CapabilityManager.get(new CapabilityToken<>() {});

    private PetalCapabilities() {}
}

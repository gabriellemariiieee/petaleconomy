package io.petaleconomy.capabilities;

import io.petaleconomy.balance.PetalBalance;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class PetalCapabilities {

    public static final Capability<PetalBalance> PETAL_BALANCE = CapabilityManager.get(new CapabilityToken<>() {});

    private PetalCapabilities() {}
}
package io.petaleconomy.capabilities;

import io.petaleconomy.economy.PrimaryPetalAccount;
import io.petaleconomy.item.handler.PortableAPDItemHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class PetalCapabilities {

    public static final Capability<PrimaryPetalAccount> PRIMARY_PETAL_ACCOUNT = CapabilityManager.get(new CapabilityToken<>() {});

    public static final Capability<PortableAPDItemHandler> PORTABLE_APD_ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});

    private PetalCapabilities() {}
}
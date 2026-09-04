package io.petaleconomy.network;

import net.minecraft.network.FriendlyByteBuf;

public class SyncAccountNamePacket {
    private final String accountName;

    public SyncAccountNamePacket(String accountName) {
        this.accountName = accountName;
    }

    public SyncAccountNamePacket(FriendlyByteBuf buf) {
        this.accountName = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(accountName);
    }

    public String getAccountName() {
        return accountName;
    }
}

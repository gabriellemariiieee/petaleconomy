package io.petaleconomy.economy;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class PrimaryPetalAccount {
    private UUID accountId;

    public PrimaryPetalAccount() {
        this.accountId = null;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public boolean hasPrimaryAccount() {
        return this.accountId != null;
    }
}

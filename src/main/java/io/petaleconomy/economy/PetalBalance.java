package io.petaleconomy.economy;

import java.util.UUID;

public class PetalBalance {
    private UUID accountId;

    public PetalBalance() { }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }
}

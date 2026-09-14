package io.blossombree.petaleconomy.util;

import net.minecraft.util.StringRepresentable;

public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    CREATE_ACCOUNT("Create Account");

    private final String name;

    private TransactionType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static TransactionType byName(String type) {
        switch (type) {
            case "Deposit":
                return DEPOSIT;
            case "Withdraw":
                return WITHDRAW;
            case "Create Account":
                return CREATE_ACCOUNT;
        }
        return null;
    }
}

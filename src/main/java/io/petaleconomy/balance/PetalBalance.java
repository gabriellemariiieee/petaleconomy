package io.petaleconomy.balance;

public class PetalBalance {
    private int balance = 0;

    public PetalBalance() { }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int amount) {
        this.balance = amount;
    }

    public void addPetals(int amount) {
        this.balance += amount;
    }

    public boolean removePetals(int amount) {
        if (balance < amount) {
            return false;
        }
        this.balance -= amount;
        return true;
    }
}

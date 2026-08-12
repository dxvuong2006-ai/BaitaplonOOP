package com.expensemanager.model.wallet;

import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.exception.EmptyFieldException;

import java.util.Objects;

public abstract class Wallet {

    private String id;
    private String name;
    protected double balance;
    private final WalletType type;
    private int userId;                                            // MỚI

    public Wallet(String id, String name, double balance, WalletType type, int userId) { // sửa: +userId
        if (name == null || name.isBlank()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        if (balance < 0) {
            throw new NegativeValueException(FieldType.BALANCE);
        }
        if (type == null) {
            throw new EmptyFieldException(FieldType.WALLETTYPE);
        }
        setId(id);
        this.name = name;
        this.balance = balance;
        this.type = type;
        setUserId(userId);
    }

    public String getId() { return id; }

    public void setId(String id) {
        if (id == null) {
            throw new EmptyFieldException(FieldType.ID);
        }
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        this.name = name;
    }

    public double getBalance() { return balance; }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new NegativeValueException(FieldType.BALANCE);
        }
        this.balance = balance;
    }

    public WalletType getType() { return type; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new EmptyFieldException(FieldType.USERID);
        }
        this.userId = userId;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new NegativeValueException(FieldType.AMOUNT);
        }
        this.balance += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wallet)) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract void withdraw(double amount);
}

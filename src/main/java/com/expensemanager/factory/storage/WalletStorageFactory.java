package com.expensemanager.factory.storage;

import com.expensemanager.factory.model.WalletFactory;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.EWallet;
import com.expensemanager.model.enums.WalletType;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public class WalletStorageFactory extends AbstractStorageFactory<Wallet> {

    @Override
    protected TypeToken<List<Wallet>> getTypeToken() {
        return new TypeToken<List<Wallet>>() {};
    }

    @Override
    protected String[] getCsvHeader() {
        return new String[]{"id", "name", "balance", "type", "extraFee"};
    }

    @Override
    protected Function<Wallet, String[]> getSerializer() {
        return wallet -> new String[]{
                wallet.getId(),
                wallet.getName(),
                String.valueOf(wallet.getBalance()),
                wallet.getType().name(),
                String.valueOf(extractWalletExtraFee(wallet))
        };
    }

    @Override
    protected Function<String[], Wallet> getDeserializer() {
        return row -> {
            String id = row[0];
            String name = row[1];
            double balance = Double.parseDouble(row[2]);
            WalletType type = WalletType.valueOf(row[3]);
            double extraFee = row.length > 4 ? Double.parseDouble(row[4]) : 0.0;

            return WalletFactory.createWallet(id, name, balance, type, extraFee);
        };
    }

    private double extractWalletExtraFee(Wallet wallet) {
        if (wallet instanceof BankAccount) return ((BankAccount) wallet).getTransactionFee();
        if (wallet instanceof EWallet) return ((EWallet) wallet).getFeePercent();
        return 0.0;
    }
}
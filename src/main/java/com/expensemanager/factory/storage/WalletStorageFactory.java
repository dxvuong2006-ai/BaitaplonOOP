package com.expensemanager.factory.storage;

import com.expensemanager.factory.model.WalletFactory;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.EWallet;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public class WalletStorageFactory extends AbstractStorageFactory<Wallet> {

    private Storage<Wallet> storage;

    public WalletStorageFactory(StorageType storageType) {
        super(storageType);
    }

    @Override
    protected TypeToken<List<Wallet>> getTypeToken() {
        return new TypeToken<List<Wallet>>() {};
    }

    @Override
    protected String[] getCsvHeader() {
        return new String[]{"id", "name", "balance", "type", "extraFee", "userId"};
    }

    @Override
    protected Function<Wallet, String[]> getSerializer() {
        return wallet -> new String[]{
                wallet.getId(),
                wallet.getName(),
                String.valueOf(wallet.getBalance()),
                wallet.getType().name(),
                String.valueOf(extractWalletExtraFee(wallet)),
                String.valueOf(wallet.getUserId())
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
            int userId = row.length > 5 ? Integer.parseInt(row[5]) : 0;

            return WalletFactory.createWallet(id, name, balance, type, extraFee, userId);
        };
    }

    private double extractWalletExtraFee(Wallet wallet) {
        if (wallet instanceof BankAccount) return ((BankAccount) wallet).getTransactionFee();
        if (wallet instanceof EWallet) return ((EWallet) wallet).getFeePercent();
        return 0.0;
    }
}

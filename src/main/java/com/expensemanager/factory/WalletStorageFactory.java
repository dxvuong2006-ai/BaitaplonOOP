package com.expensemanager.factory;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.EWallet;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.repository.CsvStorage;
import com.expensemanager.repository.JsonStorage;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public final class WalletStorageFactory {

    private WalletStorageFactory() {
        // Không cho phép khởi tạo Factory
    }

    public static Storage<Wallet> createStorage(StorageType type) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.STORAGETYPE);
        }

        switch (type) {
            case CSV:
                String[] header = {"id", "name", "balance", "type", "extraFee"};

                Function<Wallet, String[]> serializer = wallet -> new String[]{
                        wallet.getId(),
                        wallet.getName(),
                        String.valueOf(wallet.getBalance()),
                        wallet.getType().name(),
                        String.valueOf(extractWalletExtraFee(wallet))
                };

                Function<String[], Wallet> deserializer = row -> {
                    String id = row[0];
                    String name = row[1];
                    double balance = Double.parseDouble(row[2]);
                    WalletType walletType = WalletType.valueOf(row[3]);
                    double extraFee = row.length > 4 ? Double.parseDouble(row[4]) : 0.0;

                    return WalletFactory.createWallet(id, name, balance, walletType, extraFee);
                };

                return new CsvStorage<>(header, serializer, deserializer);

            case JSON:
                return new JsonStorage<>(new TypeToken<List<Wallet>>() {});

            default:
                throw new UnsupportedOperationException("Chưa hỗ trợ định dạng storage: " + type);
        }
    }

    private static double extractWalletExtraFee(Wallet wallet) {
        if (wallet instanceof BankAccount) {
            return ((BankAccount) wallet).getTransactionFee();
        } else if (wallet instanceof EWallet) {
            return ((EWallet) wallet).getFeePercent();
        }
        return 0.0;
    }
}
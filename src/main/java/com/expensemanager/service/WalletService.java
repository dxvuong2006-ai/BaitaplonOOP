package com.expensemanager.service;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.factory.storage.WalletStorageFactory;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.FilePath;
import com.expensemanager.model.wallet.Wallet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Quản lý ví. */
public class WalletService {

    private final WalletStorageFactory storageFactory;
    private List<Wallet> wallets = new ArrayList<>();

    public WalletService(WalletStorageFactory storageFactory){
        this.storageFactory = storageFactory;
        load();
    }

    public void load() {
        wallets.clear();
        wallets.addAll(storageFactory.load(FilePath.WALLET));
    }

    public void save() {
        storageFactory.save(FilePath.WALLET, wallets);
    }

    /** Tạo loại ví mới. */
    public void addWallet (Wallet wallet) {
        ValidationService.validateWallet(wallet);
        ValidationService.validateWalletName(wallets, wallet.getName());
        wallets.add(wallet);
        save();
    }

    /** Xóa ví. */
    public void removeWallet(Wallet wallet) {
        ValidationService.validateWallet(wallet);
        wallets.remove(wallet);
        save();
    }

    /** Chỉnh sửa ví. */
    public void updateWallet(Wallet oldwallet, Wallet newWallet) {
        ValidationService.validateWallet(oldwallet);
        ValidationService.validateWallet(newWallet);
        Wallet existedWallet = findWalletByName(newWallet.getName());
        if (existedWallet != null && existedWallet != oldwallet) {
            throw new DuplicateEntityException("Ví", newWallet.getName());
        }
        oldwallet.setName(newWallet.getName());
        save();
    }

    /** Tìm kiếm ví bằng tên. */
    public Wallet findWalletByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        for (Wallet wallet : wallets) {
            if (wallet.getName().equalsIgnoreCase(name.trim())) {
                return wallet;
            }
        }
        return null;
    }

    /** Tìm kiếm ví bằng ID. */
    public Wallet findWalletById(String id) {
        if (id == null) {
            throw new EmptyFieldException(FieldType.ID);
        }
        for (Wallet wallet : wallets) {
            if (wallet.getId().equals(id)) {
                return wallet;
            }
        }
        return null;
    }

    /** Trả về danh sách ví. */
    public List<Wallet> getWallets() {
        return Collections.unmodifiableList(wallets);
    }
}

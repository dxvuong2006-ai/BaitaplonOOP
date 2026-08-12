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
    public void addWallet (Wallet wallet, int userId) {
        ValidationService.validateWallet(wallet);
        wallet.setUserId(userId);
        if (findWalletById(wallet.getId(), userId) != null) {
            throw new DuplicateEntityException("Ví", "mã " + wallet.getId());
        }
        ValidationService.validateWalletName(getWallets(userId), wallet.getName());

        wallets.add(wallet);
        save();
    }

    /** Xóa ví. */
    public void removeWallet(Wallet wallet, int userId) {
        ValidationService.validateWallet(wallet);
        Wallet existing = findWalletById(wallet.getId(), userId);
        wallets.remove(existing);
        save();
    }

    /** Chỉnh sửa ví. */
    public void updateWallet(Wallet oldwallet, Wallet newWallet, int userId) {
        ValidationService.validateWallet(oldwallet);
        ValidationService.validateWallet(newWallet);
        Wallet existingOld = findWalletById(oldwallet.getId(), userId);
        ValidationService.validateWallet(existingOld);
        Wallet existedWallet = findWalletByName(newWallet.getName(), userId);
        if (existedWallet != null && existedWallet != oldwallet) {
            throw new DuplicateEntityException("Ví", newWallet.getName());
        }
        oldwallet.setName(newWallet.getName());
        oldwallet.setId(newWallet.getId());
        oldwallet.setUserId(newWallet.getUserId());
        save();
    }

    /** Tìm kiếm ví bằng tên. */
    public Wallet findWalletByName(String name, int userId) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        for (Wallet wallet : getWallets(userId)) {
            if (wallet.getName().equalsIgnoreCase(name.trim()) && wallet.getUserId() == userId) {
                return wallet;
            }
        }
        return null;
    }

    /** Tìm kiếm ví bằng ID. */
    public Wallet findWalletById(String id, int userId) {
        if (id == null) {
            throw new EmptyFieldException(FieldType.ID);
        }
        for (Wallet wallet : wallets) {
            if (wallet.getId().equals(id) && wallet.getUserId() == userId) {
                return wallet;
            }
        }
        return null;
    }

    /** Trả về danh sách ví. */
    public List<Wallet> getWallets(int userId) {
        return wallets.stream()
                .filter(wallet -> wallet.getUserId() == userId)
                .toList();
    }
}

package com.expensemanager.factory.storage;

import com.expensemanager.factory.model.UserFactory;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.user.User;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public class UserStorageFactory extends AbstractStorageFactory<User> {
    private Storage<User> storage;

    public UserStorageFactory(StorageType storageType) {
        super(storageType);
    }

    @Override
    protected TypeToken<List<User>> getTypeToken() {
        return new TypeToken<List<User>>() {};
    }

    @Override
    protected String[] getCsvHeader() {
        return new String[] {"id", "username", "passwordHash", "salt", "email"};
    }

    @Override
    protected Function<User, String[]> getSerializer() {
        return user -> new String[] {
                user.getId(), user.getUsername(), user.getPasswordHash(),
                user.getSalt() != null ? user.getSalt() : "",
                user.getEmail() != null ? user.getEmail() : ""
        };
    }

    @Override
    protected Function<String[], User> getDeserializer() {
        return row -> {
            String id = row[0];
            String username = row[1];
            String passwordHash = row[2];
            String salt = row.length > 3 ? row[3] : "";
            String email = row.length > 4 ? row[4] : "";
            return UserFactory.reconstructUser(id, username, passwordHash, salt, email);
        };
    }
}

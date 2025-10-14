package dataaccess;

import java.util.HashMap;

import dataobjects.UserData;

public class MemoryUserDAO implements UserDAO{
    private final HashMap<String, UserData> users;

    public MemoryUserDAO() {
        users = new HashMap<>();
    }

    public void clear() {
        users.clear();
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }
}

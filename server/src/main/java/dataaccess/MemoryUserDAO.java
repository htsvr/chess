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
    public UserData getUser(String username) throws DataAccessException{
        UserData user = users.get(username);
        if (user == null) {
            throw new DataAccessException("user doesn't exist");
        }
        return user;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException{
        if(users.get(user.username()) != null) {
            throw new DataAccessException("user already exists");
        }
        users.put(user.username(), user);
    }
}

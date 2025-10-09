package dataaccess;

import dataobjects.*;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO{
    private final ArrayList<UserData> users;

    public MemoryUserDAO() {
        users = new ArrayList<>();
    }

    @Override
    public UserData getUser(String username) {
        for (UserData user:users){
            if(user.username().equals(username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public void createUser(UserData user) {
        users.add(user);
    }
}

package dataaccess;

import java.util.ArrayList;
import dataobjects.UserData;

public class MemoryUserDAO implements UserDAO{
    private final ArrayList<UserData> users;

    public MemoryUserDAO() {
        users = new ArrayList<>();
    }

    public void clear() {
        users.clear();
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

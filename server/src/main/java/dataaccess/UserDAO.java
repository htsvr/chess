package dataaccess;

import dataobjects.UserData;

public interface UserDAO {
    public UserData getUser(String username);
    public void createUser(UserData userData);
}

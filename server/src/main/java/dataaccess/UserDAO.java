package dataaccess;

import dataobjects.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData userData);
    void clear();
}

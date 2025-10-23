package dataaccess;

import dataobjects.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    void clear() throws DataAccessException;
}

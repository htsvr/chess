package dataaccess;

import dataobjects.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData authData) throws DataAccessException;
    void clear();
}

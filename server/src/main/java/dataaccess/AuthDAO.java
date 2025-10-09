package dataaccess;

import dataobjects.AuthData;

public interface AuthDAO {
    public void createAuth(AuthData authData);
    public AuthData getAuth(String authToken);
    public void deleteAuth(AuthData authData) throws DataAccessException;
}

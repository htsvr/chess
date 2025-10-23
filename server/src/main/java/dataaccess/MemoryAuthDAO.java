package dataaccess;

import dataobjects.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authList;
    public MemoryAuthDAO() {
        authList = new HashMap<>();
    }

    public void clear() {
        authList.clear();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException{
        if(authList.put(authData.authToken(), authData) != null) {
            throw new DataAccessException("duplicate authToken");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        AuthData auth = authList.get(authToken);
        if(auth == null) {
            throw new DataAccessException("Auth Token doesn't exist");
        }
        return auth;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        if (authList.remove(authData.authToken()) == null) {
            throw new DataAccessException("authData: " + authData.toString() + " does not exist");
        }
    }
}

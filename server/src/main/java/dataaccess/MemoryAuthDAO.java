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
    public void createAuth(AuthData authData) {
        authList.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authList.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        if (authList.remove(authData.authToken()) == null) {
            throw new DataAccessException("authData: " + authData.toString() + " does not exist");
        }
    }
}

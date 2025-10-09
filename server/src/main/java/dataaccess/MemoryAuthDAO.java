package dataaccess;

import dataobjects.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{
    private final ArrayList<AuthData> authList;
    public MemoryAuthDAO() {
        authList = new ArrayList<>();
    }

    public void clear() {
        authList.clear();
    }

    @Override
    public void createAuth(AuthData authData) {
        authList.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData auth:authList) {
            if (auth.authToken().equals(authToken)){
                return auth;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        if (!authList.remove(authData)) {
            throw new DataAccessException("authData: " + authData.toString() + " does not exist");
        }
    }
}

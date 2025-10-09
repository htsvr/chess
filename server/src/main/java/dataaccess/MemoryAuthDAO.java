package dataaccess;

import dataobjects.AuthData;
import dataobjects.UserData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{
    private final ArrayList<AuthData> authList;
    public MemoryAuthDAO() {
        authList = new ArrayList<>();
    }

    @Override
    public void createAuth(AuthData authData) {
        authList.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        authList.remove(authData);
    }
}

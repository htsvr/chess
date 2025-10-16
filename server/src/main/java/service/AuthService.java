package service;

import dataaccess.*;
import dataobjects.*;

import java.util.UUID;

public class AuthService {
    private static final AuthDAO AUTH_DATA_ACCESS = new MemoryAuthDAO();

    public static void clear () {
        AUTH_DATA_ACCESS.clear();
    }

    public static AuthData createAuthToken(String username) {
        AuthData auth = new AuthData(username, UUID.randomUUID().toString());
        AUTH_DATA_ACCESS.createAuth(auth);
        return auth;
    }

    public static void logoutUser(String authToken) throws UnrecognizedAuthTokenException, DataAccessException {
        AuthData auth = AUTH_DATA_ACCESS.getAuth(authToken);
        if (auth == null) {
            throw new UnrecognizedAuthTokenException("Unrecognized Auth Token: " + authToken);
        } else {
            AUTH_DATA_ACCESS.deleteAuth(auth);
        }
    }

    public static void validateAuth (String authToken) throws UnrecognizedAuthTokenException{
        if (AUTH_DATA_ACCESS.getAuth(authToken) == null) {
            throw new UnrecognizedAuthTokenException("Invalid Auth Token");
        }
    }

    public static AuthData getAuthToken(String auth) throws UnrecognizedAuthTokenException{
        validateAuth(auth);
        return AUTH_DATA_ACCESS.getAuth(auth);
    }
}

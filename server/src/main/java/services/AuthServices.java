package services;

import dataaccess.*;
import dataobjects.*;

import java.util.UUID;

public class AuthServices {
    private static final AuthDAO authDataAccess = new MemoryAuthDAO();

    public static void clear () {
        authDataAccess.clear();
    }

    public static AuthData createAuthToken(String username) {
        AuthData auth = new AuthData(username, UUID.randomUUID().toString());
        authDataAccess.createAuth(auth);
        return auth;
    }

    public static void logoutUser(String authToken) throws UnrecognizedAuthTokenException, DataAccessException {
        AuthData auth = authDataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnrecognizedAuthTokenException("Unrecognized Auth Token: " + authToken);
        } else {
            authDataAccess.deleteAuth(auth);
        }
    }

    public static void validateAuth (String authToken) throws UnrecognizedAuthTokenException{
        if (authDataAccess.getAuth(authToken) == null) {
            throw new UnrecognizedAuthTokenException("Invalid Auth Token");
        }
    }
}

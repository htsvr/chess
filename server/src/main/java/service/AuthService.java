package service;

import dataaccess.*;
import dataobjects.*;

import java.util.UUID;

public class AuthService {
    private static final AuthDAO AUTH_DATA_ACCESS = new MemoryAuthDAO();

    /**
     * clears everything in the auth data database
     */
    public static void clear () {
        AUTH_DATA_ACCESS.clear();
    }

    /**
     * generates a new auth token and stores it in the database
     * @param username username of the user
     * @return an AuthData object containing the username and generated auth token
     */
    public static AuthData createAuthToken(String username) throws DataAccessException{
        AuthData auth = new AuthData(username, UUID.randomUUID().toString());
        AUTH_DATA_ACCESS.createAuth(auth);
        return auth;
    }

    /**
     * Removes the auth data containing the auth token from the database
     * @param authToken the auth token to remove from the database
     * @throws UnrecognizedAuthTokenException if the authToken is null or not in the database
     */
    public static void logoutUser(String authToken) throws UnrecognizedAuthTokenException, DataAccessException {
        AuthData auth = AUTH_DATA_ACCESS.getAuth(authToken);
        try {
            AUTH_DATA_ACCESS.deleteAuth(auth);
        } catch (DataAccessException | NullPointerException e) {
            throw new UnrecognizedAuthTokenException("Unrecognized Auth Token: " + authToken);
        }
    }

    /**
     * Throws an UnrecognizedAuthTokenException if the auth token isn't in the database
     * @param authToken the auth token to validate
     * @throws UnrecognizedAuthTokenException if the auth token isn't in the database
     */
    public static void validateAuth (String authToken) throws UnrecognizedAuthTokenException, DataAccessException{
        if (AUTH_DATA_ACCESS.getAuth(authToken) == null) {
            throw new UnrecognizedAuthTokenException("Invalid Auth Token");
        }
    }

    /**
     * gets the AuthData abject with the given auth token
     * @param auth the auth token to look up
     * @return and AuthData object containing the given auth token
     * @throws UnrecognizedAuthTokenException if the auth token isn't in the database
     */
    public static AuthData getAuthToken(String auth) throws UnrecognizedAuthTokenException, DataAccessException{
        validateAuth(auth);
        return AUTH_DATA_ACCESS.getAuth(auth);
    }
}

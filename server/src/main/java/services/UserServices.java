package services;

import dataaccess.*;
import dataobjects.*;

import java.util.UUID;

public class UserServices {
    private static final UserDAO userDataAccess = new MemoryUserDAO();
    private static final AuthDAO authDataAccess = new MemoryAuthDAO();

    public static void clear () {
        userDataAccess.clear();
        authDataAccess.clear();
    }

    public static AuthData registerUser(UserData user) throws AlreadyTakenException {
        if(userDataAccess.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }

        userDataAccess.createUser(user);
        return createAuthToken(user.username());
    }

    private static AuthData createAuthToken(String username) {
        AuthData auth = new AuthData(username, UUID.randomUUID().toString());
        authDataAccess.createAuth(auth);
        return auth;
    }

    public static AuthData loginUser(LoginRequest req) throws IncorrectUsernameOrPasswordException{
        UserData user = userDataAccess.getUser(req.username());
        if(user == null) {
            throw new IncorrectUsernameOrPasswordException("Username doesn't exist");
        }
        if(!user.password().equals(req.password())) {
            throw new IncorrectUsernameOrPasswordException("Incorrect Username or Password");
        }
        return createAuthToken(req.username());
    }

    public static void logoutUser(String authToken) throws UnrecognizedAuthTokenException, DataAccessException {
        AuthData auth = authDataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnrecognizedAuthTokenException("Unrecognized Auth Token: " + authToken);
        } else {
            authDataAccess.deleteAuth(auth);
        }
    }
}

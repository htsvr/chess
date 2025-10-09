package services;

import dataaccess.*;
import dataobjects.*;
import java.util.UUID;

public class UserServices {
    private static final UserDAO userDataAccess = new MemoryUserDAO();
    private static final AuthDAO authDataAccess = new MemoryAuthDAO();

    public static AuthData registerUser(UserData user) throws AlreadyTakenException {
        if(userDataAccess.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }

        userDataAccess.createUser(user);
        AuthData auth = new AuthData(user.username(), UUID.randomUUID().toString());
        authDataAccess.createAuth(auth);
        return auth;
    }

    public static AuthData login(LoginRequest req) throws IncorrectUsernameOrPasswordException{
        return null;
    }
}

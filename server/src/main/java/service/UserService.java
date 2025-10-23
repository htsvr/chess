package service;

import dataaccess.*;
import dataobjects.*;

public class UserService {
    private static final UserDAO USER_DATA_ACCESS = new MemoryUserDAO();

    /**
     * clears everything in the user data database
     */
    public static void clear () {
        USER_DATA_ACCESS.clear();
    }

    /**
     * Registers a new user
     * @param user a UserData object containing the username, email, and password of the new user
     * @return an AuthData object containing the username generated auth token
     * @throws AlreadyTakenException if a user with the given username already exists
     */
    public static AuthData registerUser(UserData user) throws AlreadyTakenException, DataAccessException {
        if(USER_DATA_ACCESS.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }

        USER_DATA_ACCESS.createUser(user);
        return AuthService.createAuthToken(user.username());
    }


    /**
     * logs in a user
     * @param req LoginRequest containing the username and password
     * @return an AuthData object for the username
     * @throws IncorrectUsernameOrPasswordException if the username and password don't match
     */
    public static AuthData loginUser(LoginRequest req) throws IncorrectUsernameOrPasswordException, DataAccessException{
        UserData user = USER_DATA_ACCESS.getUser(req.username());
        if(user == null) {
            throw new IncorrectUsernameOrPasswordException("Username doesn't exist");
        }
        if(!user.password().equals(req.password())) {
            throw new IncorrectUsernameOrPasswordException("Incorrect Username or Password");
        }
        return AuthService.createAuthToken(req.username());
    }
}

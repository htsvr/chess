package service;

import dataaccess.*;
import dataobjects.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private static final UserDAO USER_DATA_ACCESS;

    static {
        try {
            USER_DATA_ACCESS = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * clears everything in the user data database
     */
    public static void clear () throws DataAccessException{
        USER_DATA_ACCESS.clear();
    }

    /**
     * Registers a new user
     * @param user a UserData object containing the username, email, and password of the new user
     * @return an AuthData object containing the username generated auth token
     * @throws AlreadyTakenException if a user with the given username already exists
     */
    public static AuthData registerUser(UserData user) throws AlreadyTakenException, DataAccessException {
        if(USER_DATA_ACCESS.getUser(user.username()) == null) {
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            USER_DATA_ACCESS.createUser(new UserData(user.username(), hashedPassword, user.email()));
            return AuthService.createAuthToken(user.username());
        } else {
            throw new AlreadyTakenException("Username already taken");
        }
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
        if(!BCrypt.checkpw(req.password(), user.password())) {
            throw new IncorrectUsernameOrPasswordException("Incorrect Username or Password");
        }
        return AuthService.createAuthToken(req.username());
    }
}

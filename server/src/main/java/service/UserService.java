package service;

import dataaccess.*;
import dataobjects.*;

public class UserService {
    private static final UserDAO USER_DATA_ACCESS = new MemoryUserDAO();

    public static void clear () {
        USER_DATA_ACCESS.clear();
    }

    public static AuthData registerUser(UserData user) throws AlreadyTakenException {
        if(USER_DATA_ACCESS.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }

        USER_DATA_ACCESS.createUser(user);
        return AuthService.createAuthToken(user.username());
    }



    public static AuthData loginUser(LoginRequest req) throws IncorrectUsernameOrPasswordException{
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

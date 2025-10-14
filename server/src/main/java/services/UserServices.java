package services;

import dataaccess.*;
import dataobjects.*;

public class UserServices {
    private static final UserDAO userDataAccess = new MemoryUserDAO();

    public static void clear () {
        userDataAccess.clear();
    }

    public static AuthData registerUser(UserData user) throws AlreadyTakenException {
        if(userDataAccess.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }

        userDataAccess.createUser(user);
        return AuthServices.createAuthToken(user.username());
    }



    public static AuthData loginUser(LoginRequest req) throws IncorrectUsernameOrPasswordException{
        UserData user = userDataAccess.getUser(req.username());
        if(user == null) {
            throw new IncorrectUsernameOrPasswordException("Username doesn't exist");
        }
        if(!user.password().equals(req.password())) {
            throw new IncorrectUsernameOrPasswordException("Incorrect Username or Password");
        }
        return AuthServices.createAuthToken(req.username());
    }
}

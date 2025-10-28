package dataaccess;

import dataobjects.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, SQLUserDAO.class})
    public void getUserSuccess(Class<? extends UserDAO> daoClass) throws Exception {
        UserDAO dataAccess = daoClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String username = "exampleName";
        String email = "example@test.com";
        String password = "pasword123";
        UserData user = new UserData(username, password, email);

        String username2 = "newName";
        String email2 = "newemail@test.com";
        String password2 = "helloWorld";
        UserData user2 = new UserData(username2, password2, email2);
        dataAccess.createUser(user);
        dataAccess.createUser(user2);
        assertEquals(user, dataAccess.getUser(username));
        assertEquals(user2, dataAccess.getUser(username2));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, SQLUserDAO.class})
    public void getUserFailure(Class<? extends UserDAO> daoClass) throws Exception {
        UserDAO dataAccess = daoClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String username = "exampleName";
        assertNull(dataAccess.getUser(username));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, SQLUserDAO.class})
    public void createUserSuccess(Class<? extends UserDAO> daoClass) throws Exception {
        UserDAO dataAccess = daoClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String username = "exampleName";
        String email = "example@test.com";
        String password = "pasword123";
        UserData user = new UserData(username, password, email);
        dataAccess.createUser(user);
        assertEquals(user, dataAccess.getUser(username));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, SQLUserDAO.class})
    public void createUserFailure(Class<? extends UserDAO> daoClass) throws Exception {
        UserDAO dataAccess = daoClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String username = "exampleName";
        String email = "example@test.com";
        String password = "pasword123";

        String email2 = "newemail@test.com";
        String password2 = "helloWorld";
        UserData user = new UserData(username, password, email);
        UserData user2 = new UserData(username, password2, email2);
        dataAccess.createUser(user);
        assertThrows(DataAccessException.class, () -> dataAccess.createUser(user2));
        assertEquals(user, dataAccess.getUser(username));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, SQLUserDAO.class})
    public void clearSuccess(Class<? extends UserDAO> daoClass) throws Exception {
        UserDAO dataAccess = daoClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String username = "exampleName";
        String email = "example@test.com";
        String password = "pasword123";
        UserData user = new UserData(username, password, email);
        dataAccess.createUser(user);
        dataAccess.clear();
        assertNull(dataAccess.getUser(username));
    }
}

package dataaccess;

import dataobjects.AuthData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    public void createAuthSuccess(Class<? extends AuthDAO> DAOClass) throws Exception {
        AuthDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String user = "testUser";
        String authToken = UUID.randomUUID().toString();
        AuthData data = new AuthData(user, authToken);
        dataAccess.createAuth(data);
        assertEquals(data, dataAccess.getAuth(authToken));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    public void createAuthFailure(Class<? extends AuthDAO> DAOClass) throws Exception {
        AuthDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String user = "testUser";
        String authToken = UUID.randomUUID().toString();
        AuthData data = new AuthData(user, authToken);
        dataAccess.createAuth(data);
        String user2 = "differentUser";
        AuthData data2 = new AuthData(user2, authToken);
        assertThrows(DataAccessException.class, () -> dataAccess.createAuth(data2));
        assertEquals(data, dataAccess.getAuth(authToken));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    public void clearSuccess(Class<? extends AuthDAO> DAOClass) throws Exception {
        AuthDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        String user = "testUser";
        String authToken = UUID.randomUUID().toString();
        AuthData data = new AuthData(user, authToken);
        dataAccess.createAuth(data);
        dataAccess.clear();
        assertThrows(DataAccessException.class, () -> dataAccess.getAuth(authToken));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    public void getAuthSuccess(Class<? extends AuthDAO> DAOClass) throws Exception {
        AuthDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String user1 = "testUser1";
        String authToken1 = UUID.randomUUID().toString();
        AuthData data1 = new AuthData(user1, authToken1);
        dataAccess.createAuth(data1);

        String user2 = "testUser2";
        String authToken2 = UUID.randomUUID().toString();
        AuthData data2 = new AuthData(user2, authToken2);
        dataAccess.createAuth(data2);

        assertEquals(data1, dataAccess.getAuth(authToken1));
        assertEquals(data2, dataAccess.getAuth(authToken2));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    public void getAuthFailure(Class<? extends AuthDAO> DAOClass) throws Exception {
        AuthDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String authToken = UUID.randomUUID().toString();

        assertThrows(DataAccessException.class, () -> dataAccess.getAuth(authToken));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    public void deleteAuthSuccess(Class<? extends AuthDAO> DAOClass) throws Exception {
        AuthDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String user = "testUser";
        String authToken = UUID.randomUUID().toString();
        AuthData data = new AuthData(user, authToken);
        dataAccess.createAuth(data);
        dataAccess.deleteAuth(data);
        assertThrows(DataAccessException.class, () -> dataAccess.getAuth(authToken));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    public void deleteAuthFailure(Class<? extends AuthDAO> DAOClass) throws Exception {
        AuthDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        String user = "testUser";
        String authToken = UUID.randomUUID().toString();
        AuthData data = new AuthData(user, authToken);
        assertThrows(DataAccessException.class, () -> dataAccess.deleteAuth(data));
    }
}

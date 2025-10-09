package unitTests.services;

import org.junit.jupiter.api.*;
import dataobjects.*;
import services.*;

import static services.UserServices.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitTests {

    @BeforeAll
    public static void init() {

    }

    @AfterAll
    public static void stop() {

    }

    @Test
    @Order(1)
    public void registerSuccess() {
        String username = "abcdef";
        String password = "password123";
        String email = "ex@example.com";
        Assertions.assertDoesNotThrow(() -> registerUser(new UserData(username, password, email)));
    }

    @Test
    @Order(2)
    public void registerFalure() {
        String username = "abcdef";
        String password = "password123";
        String email = "ex@example.com";
        UserData user = new UserData(username, password, email);
        try {
            registerUser(user);
        } catch (AlreadyTakenException e){

        }
        Assertions.assertThrows(AlreadyTakenException.class, () -> registerUser(user));
    }

    @Test
    @Order(3)
    public void logInFalure() {
        String username = "newUsername";
        String password = "password123";
        LoginRequest req = new LoginRequest(username, password);
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> login(req));
    }

    @Test
    @Order(4)
    public void logInFalureWithIncorrectPassword() {
        String username = "CowsCanFly";
        String password = "helloWorld";
        String email = "123@example.com";
        Assertions.assertDoesNotThrow(() -> registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, "badPassword");
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> login(req));
    }

    @Test
    @Order(5)
    public void logInSuccess() {
        String username = "CowsCanNotFly";
        String password = "hiEarth";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, "hiEarth");
        Assertions.assertNotNull(Assertions.assertDoesNotThrow(() -> login(req)));
    }
}

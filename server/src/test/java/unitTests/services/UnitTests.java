package unitTests.services;

import dataobjects.*;
import org.junit.jupiter.api.*;
import services.*;

import java.util.UUID;

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
        } catch (AlreadyTakenException e) {

        }
        Assertions.assertThrows(AlreadyTakenException.class, () -> registerUser(user));
    }

    @Test
    @Order(3)
    public void logInFalure() {
        String username = "newUsername";
        String password = "password123";
        LoginRequest req = new LoginRequest(username, password);
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> loginUser(req));
    }

    @Test
    @Order(4)
    public void logInFalureWithIncorrectPassword() {
        String username = "CowsCanFly";
        String password = "helloWorld";
        String email = "123@example.com";
        Assertions.assertDoesNotThrow(() -> registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, "badPassword");
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> loginUser(req));
    }

    @Test
    @Order(5)
    public void logInSuccess() {
        String username = "CowsCanNotFly";
        String password = "hiEarth";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, "hiEarth");
        Assertions.assertNotNull(Assertions.assertDoesNotThrow(() -> loginUser(req)));
    }

    @Test
    @Order(6)
    public void LogoutSuccess() {
        String username = "PinkFluffyUnicorns";
        String password = "dancingOnRainbows";
        String email = "test@example.com";
        AuthData auth = Assertions.assertDoesNotThrow(() -> registerUser(new UserData(username, password, email)));
        Assertions.assertDoesNotThrow(() -> logoutUser(auth.authToken()));
    }

    @Test
    @Order(7)
    public void LogoutFailure() {
        String username = "ThisIsMyUsername";
        String password = "andThisIsMyPassword";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> registerUser(new UserData(username, password, email)));
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> logoutUser(UUID.randomUUID().toString()));
    }

    @Test
    @Order(8)
    public void clearSuccess() {
        String username = "testName";
        String password = "examplePass";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, password);
        clear();
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> loginUser(req));
    }
}

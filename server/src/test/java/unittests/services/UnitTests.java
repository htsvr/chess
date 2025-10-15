package unittests.services;

import dataaccess.DataAccessException;
import dataobjects.*;
import org.junit.jupiter.api.*;
import services.*;
import chess.*;

import java.util.Collection;
import java.util.UUID;

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
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData(username, password, email)));
    }

    @Test
    @Order(2)
    public void registerFailure() {
        String username = "abcdef";
        String password = "password123";
        String email = "ex@example.com";
        UserData user = new UserData(username, password, email);
        try {
            UserServices.registerUser(user);
        } catch (AlreadyTakenException e) {

        }
        Assertions.assertThrows(AlreadyTakenException.class, () -> UserServices.registerUser(user));
    }

    @Test
    @Order(3)
    public void logInFailure() {
        String username = "newUsername";
        String password = "password123";
        LoginRequest req = new LoginRequest(username, password);
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> UserServices.loginUser(req));
    }

    @Test
    @Order(4)
    public void logInFailureWithIncorrectPassword() {
        String username = "CowsCanFly";
        String password = "helloWorld";
        String email = "123@example.com";
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, "badPassword");
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> UserServices.loginUser(req));
    }

    @Test
    @Order(5)
    public void logInSuccess() {
        String username = "CowsCanNotFly";
        String password = "hiEarth";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, "hiEarth");
        Assertions.assertNotNull(Assertions.assertDoesNotThrow(() -> UserServices.loginUser(req)));
    }

    @Test
    @Order(6)
    public void logoutSuccess() {
        String username = "PinkFluffyUnicorns";
        String password = "dancingOnRainbows";
        String email = "test@example.com";
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData(username, password, email)));
        Assertions.assertDoesNotThrow(() -> AuthServices.logoutUser(auth.authToken()));
    }

    @Test
    @Order(7)
    public void logoutFailure() {
        String username = "ThisIsMyUsername";
        String password = "andThisIsMyPassword";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData(username, password, email)));
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> AuthServices.logoutUser(UUID.randomUUID().toString()));
    }

    @Test
    @Order(8)
    public void clearSuccess() {
        String username = "testName";
        String password = "examplePass";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, password);
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> UserServices.loginUser(req));
    }

    @Test
    @Order(9)
    public void listGamesSuccess() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserServices.loginUser(new LoginRequest("bear", "giraffe")));
        Assertions.assertEquals(0, Assertions.assertDoesNotThrow(() -> GameServices.listGames(auth.authToken()).size()));
    }

    @Test
    @Order(10)
    public void listGamesFailureWithIncorrectAuthToken() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> GameServices.listGames("IncorrectAuth"));
    }

    @Test
    @Order(11)
    public void createGameFailureWithIncorrectAuthToken() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> GameServices.createGame("game1", "IncorrectAuth"));
    }

    @Test
    @Order(12)
    public void createGameSuccess() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserServices.loginUser(new LoginRequest("bear", "giraffe")));
        int gameID1 = Assertions.assertDoesNotThrow(() -> GameServices.createGame("game1", auth.authToken()));
        int gameID2 = Assertions.assertDoesNotThrow(() -> GameServices.createGame("game2", auth.authToken()));
        Assertions.assertNotEquals(gameID2, gameID1);
    }

    @Test
    @Order(13)
    public void createGameAndListGames() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserServices.loginUser(new LoginRequest("bear", "giraffe")));
        Assertions.assertDoesNotThrow(() -> GameServices.createGame("game1", auth.authToken()));
        Assertions.assertDoesNotThrow(() -> GameServices.createGame("game2", auth.authToken()));
        Collection<GameData> games = Assertions.assertDoesNotThrow(() -> GameServices.listGames(auth.authToken()));
        Assertions.assertNotNull(games);
        Assertions.assertEquals(2, games.size());
    }

    @Test
    @Order(14)
    public void joinGameSuccess() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserServices.loginUser(new LoginRequest("bear", "giraffe")));
        int gameID1 = Assertions.assertDoesNotThrow(() -> GameServices.createGame("game1", auth.authToken()));
        Assertions.assertDoesNotThrow(() -> GameServices.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, gameID1, auth.authToken())));
        GameData correctGameData = new GameData(gameID1, "bear", null, "game1", new ChessGame());
        Assertions.assertTrue(Assertions.assertDoesNotThrow(() -> GameServices.listGames(auth.authToken()).contains(correctGameData)));
    }

    @Test
    @Order(15)
    public void joinGameFailureAlreadyTaken() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserServices.loginUser(new LoginRequest("bear", "giraffe")));
        int gameID1 = Assertions.assertDoesNotThrow(() -> GameServices.createGame("game1", auth.authToken()));
        Assertions.assertDoesNotThrow(() -> GameServices.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, gameID1, auth.authToken())));
        GameData correctGameData = new GameData(gameID1, "bear", null, "game1", new ChessGame());
        Assertions.assertTrue(Assertions.assertDoesNotThrow(() -> GameServices.listGames(auth.authToken()).contains(correctGameData)));
        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, gameID1, auth.authToken());
        Assertions.assertThrows(AlreadyTakenException.class, () -> GameServices.joinGame(req));
    }

    @Test
    @Order(16)
    public void joinGameFailureInvalidAuthToken() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserServices.loginUser(new LoginRequest("bear", "giraffe")));
        int gameID1 = Assertions.assertDoesNotThrow(() -> GameServices.createGame("game1", auth.authToken()));
        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, gameID1, "1532");
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> GameServices.joinGame(req));
    }

    @Test
    @Order(17)
    public void joinGameFailureInvalidGameID() {
        UserServices.clear();
        AuthServices.clear();
        GameServices.clear();
        Assertions.assertDoesNotThrow(() -> UserServices.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserServices.loginUser(new LoginRequest("bear", "giraffe")));
        Assertions.assertDoesNotThrow(() -> GameServices.createGame("game1", auth.authToken()));
        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, 847, auth.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> GameServices.joinGame(req));
    }
}

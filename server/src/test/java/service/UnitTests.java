package service;

import dataaccess.DataAccessException;
import dataobjects.*;
import org.junit.jupiter.api.*;
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
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData(username, password, email)));
    }

    @Test
    @Order(2)
    public void registerFailure() throws Exception{
        String username = "abcdef";
        String password = "password123";
        String email = "ex@example.com";
        UserData user = new UserData(username, password, email);
        try {
            UserService.registerUser(user);
        } catch (AlreadyTakenException e) {

        }
        Assertions.assertThrows(AlreadyTakenException.class, () -> UserService.registerUser(user));
    }

    @Test
    @Order(3)
    public void logInFailure() {
        String username = "newUsername";
        String password = "password123";
        LoginRequest req = new LoginRequest(username, password);
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> UserService.loginUser(req));
    }

    @Test
    @Order(4)
    public void logInFailureWithIncorrectPassword() {
        String username = "CowsCanFly";
        String password = "helloWorld";
        String email = "123@example.com";
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, "badPassword");
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> UserService.loginUser(req));
    }

    @Test
    @Order(5)
    public void logInSuccess() {
        String username = "CowsCanNotFly";
        String password = "hiEarth";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, "hiEarth");
        Assertions.assertNotNull(Assertions.assertDoesNotThrow(() -> UserService.loginUser(req)));
    }

    @Test
    @Order(6)
    public void logoutSuccess() {
        String username = "PinkFluffyUnicorns";
        String password = "dancingOnRainbows";
        String email = "test@example.com";
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData(username, password, email)));
        Assertions.assertDoesNotThrow(() -> AuthService.logoutUser(auth.authToken()));
    }

    @Test
    @Order(7)
    public void logoutFailure() {
        String username = "ThisIsMyUsername";
        String password = "andThisIsMyPassword";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData(username, password, email)));
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> AuthService.logoutUser(UUID.randomUUID().toString()));
    }

    @Test
    @Order(8)
    public void clearSuccess() {
        String username = "testName";
        String password = "examplePass";
        String email = "test@example.com";
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData(username, password, email)));
        LoginRequest req = new LoginRequest(username, password);
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertThrows(IncorrectUsernameOrPasswordException.class, () -> UserService.loginUser(req));
    }

    @Test
    @Order(9)
    public void listGamesSuccess() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserService.loginUser(new LoginRequest("bear", "giraffe")));
        Assertions.assertEquals(0, Assertions.assertDoesNotThrow(() -> GameService.listGames(auth.authToken()).size()));
    }

    @Test
    @Order(10)
    public void listGamesFailureWithIncorrectAuthToken() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> GameService.listGames("IncorrectAuth"));
    }

    @Test
    @Order(11)
    public void createGameFailureWithIncorrectAuthToken() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> GameService.createGame("game1", "IncorrectAuth"));
    }

    @Test
    @Order(12)
    public void createGameSuccess() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserService.loginUser(new LoginRequest("bear", "giraffe")));
        int gameID1 = Assertions.assertDoesNotThrow(() -> GameService.createGame("game1", auth.authToken()));
        int gameID2 = Assertions.assertDoesNotThrow(() -> GameService.createGame("game2", auth.authToken()));
        Assertions.assertNotEquals(gameID2, gameID1);
    }

    @Test
    @Order(13)
    public void createGameAndListGames() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserService.loginUser(new LoginRequest("bear", "giraffe")));
        Assertions.assertDoesNotThrow(() -> GameService.createGame("game1", auth.authToken()));
        Assertions.assertDoesNotThrow(() -> GameService.createGame("game2", auth.authToken()));
        Collection<GameData> games = Assertions.assertDoesNotThrow(() -> GameService.listGames(auth.authToken()));
        Assertions.assertNotNull(games);
        Assertions.assertEquals(2, games.size());
    }

    @Test
    @Order(14)
    public void joinGameSuccess() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserService.loginUser(new LoginRequest("bear", "giraffe")));
        int gameID1 = Assertions.assertDoesNotThrow(() -> GameService.createGame("game1", auth.authToken()));
        Assertions.assertDoesNotThrow(() -> GameService.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, gameID1, auth.authToken())));
        GameData correctGameData = new GameData(gameID1, "bear", null, "game1", new ChessGame());
        Assertions.assertTrue(Assertions.assertDoesNotThrow(() -> GameService.listGames(auth.authToken()).contains(correctGameData)));
    }

    @Test
    @Order(15)
    public void joinGameFailureAlreadyTaken() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserService.loginUser(new LoginRequest("bear", "giraffe")));
        int gameID1 = Assertions.assertDoesNotThrow(() -> GameService.createGame("game1", auth.authToken()));
        Assertions.assertDoesNotThrow(() -> GameService.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, gameID1, auth.authToken())));
        GameData correctGameData = new GameData(gameID1, "bear", null, "game1", new ChessGame());
        Assertions.assertTrue(Assertions.assertDoesNotThrow(() -> GameService.listGames(auth.authToken()).contains(correctGameData)));
        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, gameID1, auth.authToken());
        Assertions.assertThrows(AlreadyTakenException.class, () -> GameService.joinGame(req));
    }

    @Test
    @Order(16)
    public void joinGameFailureInvalidAuthToken() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserService.loginUser(new LoginRequest("bear", "giraffe")));
        int gameID1 = Assertions.assertDoesNotThrow(() -> GameService.createGame("game1", auth.authToken()));
        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, gameID1, "1532");
        Assertions.assertThrows(UnrecognizedAuthTokenException.class, () -> GameService.joinGame(req));
    }

    @Test
    @Order(17)
    public void joinGameFailureInvalidGameID() {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        Assertions.assertDoesNotThrow(() -> UserService.registerUser(new UserData("bear", "giraffe", "panc@ke.it")));
        AuthData auth = Assertions.assertDoesNotThrow(() -> UserService.loginUser(new LoginRequest("bear", "giraffe")));
        Assertions.assertDoesNotThrow(() -> GameService.createGame("game1", auth.authToken()));
        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, 847, auth.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> GameService.joinGame(req));
    }
}

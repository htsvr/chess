package client;

import chess.ChessGame;
import dataobjects.*;
import jakarta.websocket.DeploymentException;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade sf;

    @BeforeAll
    public static void init() throws URISyntaxException, DeploymentException, IOException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        sf = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test void clearSuccessTest() throws Exception {
        UserData user = new UserData("testUser75", "test75@Email.com", "testPassword75");
        sf.clear();
        sf.registerUser(user);
        sf.clear();
        sf.registerUser(user);
    }

    @Test
    public void registerSuccessTest() throws Exception{
        sf.clear();
        UserData user = new UserData("testUser", "test@Email.com", "testPassword");
        AuthData result = sf.registerUser(user);
        assertEquals(user.username(), result.username());
    }

    @Test
    public void registerFailureTest() throws Exception{
        sf.clear();
        UserData user = new UserData("testUser", "test@Email.com", "testPassword");
        sf.registerUser(user);
        UserData user2 = new UserData("testUser", "test2@Email.com", "testPassword2");
        assertThrows(ResponseException.class, () -> sf.registerUser(user2));
    }

    @Test
    public void loginSuccessTest() throws Exception{
        sf.clear();
        String username = "test435";
        String email = "exp23@test.com";
        String password = "PA$$WORD!";
        sf.registerUser(new UserData(username, password, email));
        AuthData result = sf.login(new LoginRequest(username, password));
        assertEquals(username, result.username());
    }

    @Test
    public void loginFailureTest() throws Exception{
        sf.clear();
        String username = "test435";
        String password = "PA$$WORD!";
        assertThrows(ResponseException.class, () -> sf.login(new LoginRequest(username, password)));
    }

    @Test
    public void logoutSuccessTest() throws Exception{
        sf.clear();
        String username = "test454";
        String email = "exp454@test.com";
        String password = "PA$$WORD!454";
        AuthData auth = sf.registerUser(new UserData(username, password, email));
        assertDoesNotThrow(() -> sf.logout(auth.authToken()));
    }

    @Test
    public void logoutFailureTest() throws Exception{
        sf.clear();
        String username = "test454";
        String email = "exp454@test.com";
        String password = "PA$$WORD!454";
        AuthData auth = sf.registerUser(new UserData(username, password, email));
        sf.logout(auth.authToken());
        assertThrows(ResponseException.class, () -> sf.logout(auth.authToken()));
    }

    @Test
    public void createGameSuccess() throws Exception{
        sf.clear();
        String username = "test454";
        String email = "exp454@test.com";
        String password = "PA$$WORD!454";
        AuthData auth = sf.registerUser(new UserData(username, password, email));
        int gameID = sf.createGame("testGame1", auth.authToken());
        assertNotEquals(0, gameID);
    }

    @Test
    public void createGameFailure() throws Exception{
        sf.clear();
        String username = "test454";
        String email = "exp454@test.com";
        String password = "PA$$WORD!454";
        AuthData auth = sf.registerUser(new UserData(username, password, email));
        sf.logout(auth.authToken());
        assertThrows(ResponseException.class, () -> sf.createGame("testGame1", auth.authToken()));
    }

    @Test
    public void listGamesSuccess() throws Exception{
        sf.clear();
        String username = "test454";
        String email = "exp454@test.com";
        String password = "PA$$WORD!454";
        String gameName = "testGame1";
        AuthData auth = sf.registerUser(new UserData(username, password, email));
        int gameID = sf.createGame(gameName, auth.authToken());
        Collection<GameData> gameList = sf.listGames(auth.authToken());
        Collection<GameData> expectedGameList = new ArrayList<>();
        expectedGameList.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        assertArrayEquals(expectedGameList.toArray(), gameList.toArray());
    }

    @Test
    public void listGamesFailure() throws Exception{
        sf.clear();
        String username = "test454";
        String email = "exp454@test.com";
        String password = "PA$$WORD!454";
        String gameName = "testGame1";
        AuthData auth = sf.registerUser(new UserData(username, password, email));
        sf.logout(auth.authToken());
        assertThrows(ResponseException.class, () -> sf.createGame(gameName, auth.authToken()));
    }

    @Test
    public void joinGameSuccess() throws Exception{
        sf.clear();
        String username = "test454";
        String email = "exp454@test.com";
        String password = "PA$$WORD!454";
        AuthData auth = sf.registerUser(new UserData(username, password, email));
        int gameID = sf.createGame("testGame1", auth.authToken());
        sf.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, gameID, auth.authToken()));
        assertEquals(username, sf.listGames(auth.authToken()).getFirst().whiteUsername());
    }

    @Test
    public void joinGameFailure() throws Exception{
        sf.clear();
        String username = "test454";
        String email = "exp454@test.com";
        String password = "PA$$WORD!454";
        AuthData auth = sf.registerUser(new UserData(username, password, email));
        int gameID = sf.createGame("testGame1", auth.authToken());
        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, gameID, auth.authToken());
        sf.joinGame(req);
        assertThrows(ResponseException.class, () -> sf.joinGame(req));
    }
}

package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataobjects.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, SQLGameDAO.class})
    public void getGamesSuccess(Class<? extends GameDAO> DAOClass) throws Exception {
        GameDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        ArrayList<GameData> games = new ArrayList<>();
        ChessGame game1 = new ChessGame();
        game1.makeMove(new ChessMove(new ChessPosition(2, 4), new ChessPosition(4, 4), null));
        ChessGame game2 = new ChessGame();
        game2.makeMove(new ChessMove(new ChessPosition(2, 6), new ChessPosition(3, 6), null));
        GameData gameData1 = new GameData(1, "whiteplayer", null, "1st game", game1);
        GameData gameData2 = new GameData(2, "whiteplayer", "black player", "2nd game", game2);
        dataAccess.clear();
        dataAccess.createGame(gameData1);
        dataAccess.createGame(gameData2);
        games.add(gameData1);
        games.add(gameData2);
        assertArrayEquals(games.toArray(), dataAccess.getGames().toArray());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, SQLGameDAO.class})
    public void getGameSuccess(Class<? extends GameDAO> DAOClass) throws Exception {
        GameDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        ChessGame game1 = new ChessGame();
        game1.makeMove(new ChessMove(new ChessPosition(2, 4), new ChessPosition(4, 4), null));
        ChessGame game2 = new ChessGame();
        game2.makeMove(new ChessMove(new ChessPosition(2, 6), new ChessPosition(3, 6), null));
        GameData gameData1 = new GameData(1, "whiteplayer", null, "1st game", game1);
        GameData gameData2 = new GameData(2, "whiteplayer", "black player", "2nd game", game2);
        dataAccess.createGame(gameData1);
        dataAccess.createGame(gameData2);
        assertEquals(gameData1, dataAccess.getGame(1));
        assertEquals(gameData2, dataAccess.getGame(2));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, SQLGameDAO.class})
    public void getGameFailure(Class<? extends GameDAO> DAOClass) throws Exception {
        GameDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        ChessGame game1 = new ChessGame();
        GameData gameData1 = new GameData(1, "whiteplayer", null, "1st game", game1);
        dataAccess.createGame(gameData1);
        assertNull(dataAccess.getGame(2));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, SQLGameDAO.class})
    public void createGameSuccess(Class<? extends GameDAO> DAOClass) throws Exception {
        GameDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        ChessGame game1 = new ChessGame();
        GameData gameData1 = new GameData(1, "whiteplayer", null, "1st game", game1);
        dataAccess.createGame(gameData1);
        assertEquals(gameData1, dataAccess.getGame(1));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, SQLGameDAO.class})
    public void createGameFailure(Class<? extends GameDAO> DAOClass) throws Exception {
        GameDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        ChessGame game1 = new ChessGame();
        GameData gameData1 = new GameData(1, "whiteplayer", null, "1st game", game1);
        dataAccess.createGame(gameData1);
        assertThrows(DataAccessException.class, () -> dataAccess.createGame(gameData1));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, SQLGameDAO.class})
    public void clearSuccess(Class<? extends GameDAO> DAOClass) throws Exception {
        GameDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        ChessGame game1 = new ChessGame();
        GameData gameData1 = new GameData(1, "whiteplayer", null, "1st game", game1);
        dataAccess.createGame(gameData1);
        dataAccess.clear();
        assertNull(dataAccess.getGame(1));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, SQLGameDAO.class})
    public void updateGameSuccess(Class<? extends GameDAO> DAOClass) throws Exception {
        GameDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        ChessGame game1 = new ChessGame();
        game1.makeMove(new ChessMove(new ChessPosition(2, 4), new ChessPosition(4, 4), null));
        GameData gameData1 = new GameData(1, "whiteplayer", null, "1st game", game1);
        ChessGame game2 = new ChessGame();
        game2.makeMove(new ChessMove(new ChessPosition(2, 6), new ChessPosition(3, 6), null));
        GameData gameData2 = new GameData(1, "whiteplayer", "black player", "2nd game", game2);

        dataAccess.createGame(gameData1);
        dataAccess.updateGame(1, gameData2);
        assertEquals(gameData2, dataAccess.getGame(1));
        assertEquals(1, dataAccess.getGames().size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, SQLGameDAO.class})
    public void updateGameFailure(Class<? extends GameDAO> DAOClass) throws Exception {
        GameDAO dataAccess = DAOClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        ChessGame game1 = new ChessGame();
        game1.makeMove(new ChessMove(new ChessPosition(2, 4), new ChessPosition(4, 4), null));
        GameData gameData1 = new GameData(1, "whiteplayer", null, "1st game", game1);
        ChessGame game2 = new ChessGame();
        game2.makeMove(new ChessMove(new ChessPosition(2, 6), new ChessPosition(3, 6), null));
        GameData gameData2 = new GameData(1, "whiteplayer", "black player", "2nd game", game2);

        dataAccess.createGame(gameData1);
        assertThrows(DataAccessException.class, () -> dataAccess.updateGame(2, gameData2));
        assertEquals(gameData1, dataAccess.getGame(1));
        assertNull(dataAccess.getGame(2));
    }
}
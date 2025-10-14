package services;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataobjects.GameData;

import java.util.Collection;
import java.util.Random;

public class GameServices {
    private static final GameDAO gameDataAccess = new MemoryGameDAO();
    private static final Random rand = new Random();

    public static void clear() {
        gameDataAccess.clear();
    }

    public static Collection<GameData> listGames(String authToken) throws UnrecognizedAuthTokenException{
        AuthServices.validateAuth(authToken);
        return gameDataAccess.getGames();
    }

    public static int createGame(String gameName, String authToken) throws UnrecognizedAuthTokenException{
        AuthServices.validateAuth(authToken);
        int gameID = rand.nextInt();
        while(gameDataAccess.getGame(gameID) != null) {
            gameID = rand.nextInt();
        }
        gameDataAccess.createGame(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }
}

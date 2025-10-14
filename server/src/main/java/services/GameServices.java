package services;

import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataobjects.GameData;

import java.util.Collection;

public class GameServices {
    private static final GameDAO gameDataAccess = new MemoryGameDAO();

    public static void clear() {
        gameDataAccess.clear();
    }

    public static Collection<GameData> listGames(String authToken) throws UnrecognizedAuthTokenException{
        AuthServices.validateAuth(authToken);
        return gameDataAccess.getGames();
    }

    public static int createGame(String gameName, String authToken) {
        return 1;
    }
}

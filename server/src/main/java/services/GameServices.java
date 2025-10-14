package services;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataobjects.GameData;
import dataobjects.JoinRequest;

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
        int gameID = rand.nextInt(99998)+1;
        while(gameDataAccess.getGame(gameID) != null) {
            gameID = rand.nextInt(99998)+1;
        }
        gameDataAccess.createGame(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    public static void joinGame(JoinRequest req) throws UnrecognizedAuthTokenException, AlreadyTakenException, DataAccessException {
        AuthServices.validateAuth(req.authToken().authToken());
        GameData game = gameDataAccess.getGame(req.gameID());
        if (game == null) {
            throw new DataAccessException("Game with gameID: " + req.gameID() + " does not exist");
        } else {
            if(req.color() == ChessGame.TeamColor.WHITE) {
                if (game.whiteUsername() == null) {
                    gameDataAccess.updateGame(req.gameID(), new GameData(req.gameID(), req.authToken().username(), game.blackUsername(), game.gameName(), game.game()));
                } else {
                    throw new AlreadyTakenException("White is already taken for gameID: " + game.gameID());
                }
            } else {
                if (game.blackUsername() == null) {
                    gameDataAccess.updateGame(req.gameID(), new GameData(req.gameID(), game.whiteUsername(), req.authToken().username(), game.gameName(), game.game()));
                } else {
                    throw new AlreadyTakenException("Black is already taken for gameID: " + game.gameID());
                }
            }

        }
    }
}

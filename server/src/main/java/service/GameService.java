package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;
import dataobjects.GameData;
import dataobjects.JoinRequest;

import java.util.Collection;
import java.util.Random;

public class GameService {
    private static final GameDAO GAME_DATA_ACCESS = new MemoryGameDAO();
    private static final Random RANDOM_GENERATOR = new Random();

    /**
     * clears everything in the game data database
     */
    public static void clear() {
        GAME_DATA_ACCESS.clear();
    }

    /**
     * lists all the games in the database
     * @param authToken valid auth token
     * @return a collection all the GameData abjects in the database
     * @throws UnrecognizedAuthTokenException if the auth token is invalid
     */
    public static Collection<GameData> listGames(String authToken) throws UnrecognizedAuthTokenException, DataAccessException{
        AuthService.validateAuth(authToken);
        return GAME_DATA_ACCESS.getGames();
    }

    /**
     * creates a game with the given name
     * @param gameName name of the game to create
     * @param authToken valid auth token
     * @return the gameID of the created game
     * @throws UnrecognizedAuthTokenException if the authToken is invalid.
     */
    public static int createGame(String gameName, String authToken) throws UnrecognizedAuthTokenException, DataAccessException{
        AuthService.validateAuth(authToken);
        int gameID = RANDOM_GENERATOR.nextInt(99998)+1;
        while(true) {
            try{
                GAME_DATA_ACCESS.getGame(gameID);
                gameID = RANDOM_GENERATOR.nextInt(99998)+1;
            } catch (DataAccessException _) {
                GAME_DATA_ACCESS.createGame(new GameData(gameID, null, null, gameName, new ChessGame()));
                break;
            }
        }
        return gameID;
    }

    /**
     * joins a game
     * @param req JoinRequest object containing the color, gameID, and authToken
     * @throws UnrecognizedAuthTokenException if the auth token is invalid
     * @throws AlreadyTakenException if the color specified is already claimed by a user
     * @throws DataAccessException if the gameID doesn't exist
     */
    public static void joinGame(JoinRequest req) throws UnrecognizedAuthTokenException, AlreadyTakenException, DataAccessException {
        String username = AuthService.getAuthToken(req.authToken()).username();
        GameData game = GAME_DATA_ACCESS.getGame(req.gameID());
        if (game == null) {
            throw new DataAccessException("Game with gameID: " + req.gameID() + " does not exist");
        } else {
            if(req.playerColor() == ChessGame.TeamColor.WHITE) {
                if (game.whiteUsername() == null) {
                    GameData updatedGame = new GameData(req.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                    GAME_DATA_ACCESS.updateGame(req.gameID(), updatedGame);
                } else {
                    throw new AlreadyTakenException("White is already taken for gameID: " + game.gameID());
                }
            } else {
                if (game.blackUsername() == null) {
                    GameData updatedGame = new GameData(req.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                    GAME_DATA_ACCESS.updateGame(req.gameID(), updatedGame);
                } else {
                    throw new AlreadyTakenException("Black is already taken for gameID: " + game.gameID());
                }
            }

        }
    }
}

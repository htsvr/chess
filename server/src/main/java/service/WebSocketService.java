package service;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SQLGameDAO;
import dataobjects.GameData;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.util.*;

public class WebSocketService {

    private final Map<Integer, ArrayList<WsMessageContext>> users;
    private final GameDAO gameDataAccess;

    public WebSocketService() {
        users = new HashMap<>();
        try {
            gameDataAccess = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<ServerMessage, Collection<WsMessageContext>> evalWsMessage(WsMessageContext ctx) {
        UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        System.out.println("received " + cmd.getCommandType() + ", " + cmd.getAuthToken() + ", " + cmd.getGameID());
        try {
            AuthService.validateAuth(cmd.getAuthToken());
            return switch (cmd.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT -> connect(ctx, cmd);
                case UserGameCommand.CommandType.LEAVE -> leave(ctx, cmd);
                case UserGameCommand.CommandType.RESIGN -> resign(ctx, cmd);
                case UserGameCommand.CommandType.MAKE_MOVE -> move(ctx, new Gson().fromJson(ctx.message(), MakeMoveCommand.class));
            };
        } catch (UnrecognizedAuthTokenException e) {
            return Map.of(new ErrorServerMessage("You are unauthorized to perform this action"), List.of(ctx));
        } catch (Exception e) {
            return Map.of(new ErrorServerMessage("Something went wrong"), List.of(ctx));
        }
    }

    private Map<ServerMessage, Collection<WsMessageContext>> resign (WsMessageContext ctx, UserGameCommand cmd)
            throws DataAccessException, UnrecognizedAuthTokenException {
        GameData game = gameDataAccess.getGame(cmd.getGameID());
        String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
        if (game.game().getIsOver()) {
            return Map.of(new ErrorServerMessage("Can't resign, game is already over"), List.of(ctx));
        } else if (Objects.equals(game.whiteUsername(), username) || Objects.equals(game.blackUsername(), username)) {
            game.game().setIsOver(true);
            gameDataAccess.updateGame(cmd.getGameID(), game);
            return Map.of(new NotificationServerMessage(username + " resigned"), users.get(cmd.getGameID()));
        } else {
            return Map.of(new ErrorServerMessage("Can't resign, game is already over"), List.of(ctx));
        }
    }

    private Map<ServerMessage, Collection<WsMessageContext>> move (WsMessageContext ctx, MakeMoveCommand cmd)
            throws DataAccessException, UnrecognizedAuthTokenException {
        GameData game = gameDataAccess.getGame(cmd.getGameID());
        String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
        try {
            if (game.game().getTeamTurn() == ChessGame.TeamColor.WHITE && Objects.equals(game.whiteUsername(), username) ||
                    game.game().getTeamTurn() == ChessGame.TeamColor.BLACK && Objects.equals(game.blackUsername(), username)) {
                game.game().makeMove(cmd.getMove());
                gameDataAccess.updateGame(cmd.getGameID(), game);
                return Map.of(
                        new NotificationServerMessage(username + " moved " + cmd.getMove().toString()),
                        getOtherUsers(cmd.getGameID(), ctx),
                        new LoadGameServerMessage(game.game()),
                        users.get(cmd.getGameID())
                );
            } else {
                return Map.of(new ErrorServerMessage("It's not your turn"), List.of(ctx));
            }
        } catch (InvalidMoveException e) {
            return Map.of(new ErrorServerMessage("Invalid Move"), List.of(ctx));
        }
    }

    private Map<ServerMessage, Collection<WsMessageContext>> leave (WsMessageContext ctx, UserGameCommand cmd)
            throws UnrecognizedAuthTokenException, DataAccessException {
        if (users.get(cmd.getGameID()).remove(ctx)) {
            String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
            GameData game = gameDataAccess.getGame(cmd.getGameID());
            if (Objects.equals(game.whiteUsername(), username)) {
                game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            } else if (Objects.equals(game.blackUsername(), username)) {
                game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            }
            gameDataAccess.updateGame(cmd.getGameID(), game);
            return Map.of(new NotificationServerMessage(username + " left"), users.get(cmd.getGameID()));
        } else {
            return Map.of(new ErrorServerMessage("You aren't in the game with gameID " + cmd.getGameID()), List.of(ctx));
        }
    }

    private Collection<WsMessageContext> getOtherUsers(int gameID, WsMessageContext ctx) {
        ArrayList<WsMessageContext> usersToNotify = new ArrayList<>();
        for (WsMessageContext user:users.get(gameID)){
            if (!user.equals(ctx)){
                usersToNotify.add(user);
            }
        }
        return usersToNotify;
    }

    private Map<ServerMessage, Collection<WsMessageContext>> connect(WsMessageContext ctx, UserGameCommand cmd) throws Exception{
        if (!users.containsKey(cmd.getGameID())) {
            users.put(cmd.getGameID(), new ArrayList<>());
        }
        users.get(cmd.getGameID()).add(ctx);
        String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
        String message;
        GameData game = gameDataAccess.getGame(cmd.getGameID());
        if (game == null) {
            return Map.of(new ErrorServerMessage("Invalid game ID"), List.of(ctx));
        } else {
            if(Objects.equals(game.whiteUsername(), username)) {
                message = username + " joined as white";
            } else if (Objects.equals(game.blackUsername(), username)) {
                message = username + " joined as black";
            } else {
                message = username + " started observing";
            }
            return Map.of(new LoadGameServerMessage(game.game()), List.of(ctx),
                    new NotificationServerMessage(message), getOtherUsers(cmd.getGameID(), ctx));
        }
    }
}
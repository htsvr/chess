package service;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SQLGameDAO;
import dataobjects.GameData;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.server.Authentication;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.util.*;

public class WebSocketService {

    private Map<Integer, ArrayList<WsMessageContext>> users;
    private final GameDAO GAME_DATA_ACCESS;

    public WebSocketService() {
        users = new HashMap<>();
        try {
            GAME_DATA_ACCESS = new SQLGameDAO();
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

    private Map<ServerMessage, Collection<WsMessageContext>> resign (WsMessageContext ctx, UserGameCommand cmd) throws DataAccessException, UnrecognizedAuthTokenException {
        GameData game = GAME_DATA_ACCESS.getGame(cmd.getGameID());
        game.game().setIsOver(true);
        String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
        return Map.of(new NotificationServerMessage(username + " resigned"), users.get(cmd.getGameID()));
    }

    private Map<ServerMessage, Collection<WsMessageContext>> move (WsMessageContext ctx, MakeMoveCommand cmd) throws DataAccessException, UnrecognizedAuthTokenException {
        GameData game = GAME_DATA_ACCESS.getGame(cmd.getGameID());
        String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
        try {
            game.game().makeMove(cmd.getMove());
            GAME_DATA_ACCESS.updateGame(cmd.getGameID(), game);
            return Map.of(
                    new NotificationServerMessage(username + " moved " + cmd.getMove().toString()),
                    getOtherUsers(cmd.getGameID(), ctx),
                    new LoadGameServerMessage(game.game()),
                    users.get(cmd.getGameID())
            );
        } catch (InvalidMoveException e) {
            return Map.of(new ErrorServerMessage("Invalid Move"), List.of(ctx));
        }
    }

    private Map<ServerMessage, Collection<WsMessageContext>> leave (WsMessageContext ctx, UserGameCommand cmd) throws UnrecognizedAuthTokenException, DataAccessException {
        if (users.get(cmd.getGameID()).remove(ctx)) {
            String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
            //TODO: remove username from gamedata
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
        GameData game = GAME_DATA_ACCESS.getGame(cmd.getGameID());
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
            return Map.of(new LoadGameServerMessage(game.game()), List.of(ctx), new NotificationServerMessage(message), getOtherUsers(cmd.getGameID(), ctx));
        }
    }
}
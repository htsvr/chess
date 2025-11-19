package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataobjects.*;
import io.javalin.*;
import io.javalin.http.Context;

import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.Session;
import service.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.sql.Array;
import java.util.*;

public class Server {

    private final Javalin server;
    private Map<Integer, ArrayList<WsMessageContext>> users;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        users = new HashMap<>();

        // Register your endpoints and exception handlers here.
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        server.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                ctx.enableAutomaticPings();
                System.out.println("Websocket connected");
            });
            ws.onMessage(this::evalWsMessage);
            ws.onClose(ctx -> System.out.println("Websocket closed"));
        });
    }

    private void evalWsMessage(WsMessageContext ctx) {
        UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        System.out.println("received " + cmd.getCommandType() + ", " + cmd.getAuthToken() + ", " + cmd.getGameID());
        //ctx.send("{\"message\":\"" + ctx.message() + "\"}");
        try {
            AuthService.validateAuth(cmd.getAuthToken());
            switch (cmd.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT -> {
                    if (!users.containsKey(cmd.getGameID())) {
                        users.put(cmd.getGameID(), new ArrayList<>());
                    }
                    users.get(cmd.getGameID()).add(ctx);
                    String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
                    String message;
                    GameData game = GameService.listGames(cmd.getAuthToken()).get(cmd.getGameID());
                    if (game == null) {
                        ctx.send(new Gson().toJson(new ErrorServerMessage("Invalid game ID")));
                    } else {
                        if(Objects.equals(game.whiteUsername(), username)) {
                            message = username + " joined as white";
                        } else if (Objects.equals(game.blackUsername(), username)) {
                            message = username + " joined as black";
                        } else {
                            message = username + " started observing";
                        }
                        ctx.send(new Gson().toJson(new LoadGameServerMessage(game.game())));
                        notifyOtherUsers(cmd.getGameID(), ctx, message);
                    }
                }
                case UserGameCommand.CommandType.LEAVE -> {
                    if (users.get(cmd.getGameID()).remove(ctx)) {
                        String username = AuthService.getAuthToken(cmd.getAuthToken()).username();
                        notifyOtherUsers(cmd.getGameID(), ctx, username + " left");
                    } else {
                        ctx.send(new Gson().toJson(new ErrorServerMessage("You aren't in the game with gameID " + cmd.getGameID())));
                    }
                }
                case UserGameCommand.CommandType.RESIGN -> {
                    //TODO: make game end
                    //TODO: notify all players
                }
                case UserGameCommand.CommandType.MAKE_MOVE -> {
                    //TODO: get board
                    //TODO: make move
                    //TODO: send board to database
                    //TODO: send move notification to other players
                    //TODO: send board to all players
                }
            }
        } catch (UnrecognizedAuthTokenException e) {
            ctx.send(new Gson().toJson(new ErrorServerMessage("You are unauthorized to perform this action")));
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorServerMessage("Something went wrong")));
        }
    }

    private void notifyAllUsers(int gameID, String message) {
        for(WsMessageContext ctxToNotify : users.get(gameID)) {
            ctxToNotify.send(new Gson().toJson(new NotificationServerMessage(message)));
        }
    }

    private void notifyOtherUsers(int gameID, WsMessageContext ctx, String message) {
        for(WsMessageContext ctxToNotify : users.get(gameID)) {
            if(ctxToNotify.session != ctx.session) {
                ctxToNotify.send(new Gson().toJson(new NotificationServerMessage(message)));
            }
        }
    }

    private void clear(Context ctx) {
        try{
            //AuthService.validateAuth(ctx.header("Authorization"));
            UserService.clear();
            AuthService.clear();
            GameService.clear();
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: something went wrong\"}");
        }
    }

    private void joinGame(Context ctx) {
        String authToken = ctx.header("Authorization");
        Gson serializer = new Gson();
        try {
            JoinRequest req = serializer.fromJson(ctx.body(), JoinRequest.class);
            if(req.playerColor() == null || req.gameID() <= 0) {
                ctx.status(400);
                ctx.result("{\"message\": \"Error: bad request\"}");
            } else {
                GameService.joinGame(new JoinRequest(req.playerColor(), req.gameID(), authToken));
                ctx.status(200);
                ctx.result("{}");
            }
        } catch (UnrecognizedAuthTokenException e) {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: unauthorized\"}");
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result("{\"message\": \"Error: already taken\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: something went wrong\"}");
        }
    }

    private void listGames(Context ctx) {
        String authToken = ctx.header("Authorization");
        Gson serializer = new Gson();
        try {
            Collection<GameData> gameList = GameService.listGames(authToken).values();
            ctx.status(200);
            ctx.result("{\"games\": " + serializer.toJson(gameList) + "}");
        } catch (UnrecognizedAuthTokenException e) {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: unauthorized\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: something went wrong\"}");
        }
    }

    private void createGame(Context ctx) {
        String authToken = ctx.header("Authorization");
        Gson serializer = new Gson();
        try {
            Map<String, String> body = serializer.fromJson(ctx.body(), Map.class);
            if (body.get("gameName") == null) {
                throw new Exception();
            } else {
                try {
                    int gameID = GameService.createGame(body.get("gameName"), authToken);
                    ctx.status(200);
                    ctx.result("{\"gameID\": " + gameID + "}");
                } catch (UnrecognizedAuthTokenException e) {
                    ctx.status(401);
                    ctx.result("{\"message\": \"Error: unauthorized\"}");
                } catch (Exception e) {
                    ctx.status(500);
                    ctx.result("{\"message\": \"Error: something went wrong\"}");
                }
            }
        } catch (Exception e){
            ctx.status(400);
            ctx.result("{\"message\": \"Error: bad request\"}");
        }
    }

    private void register(Context ctx) {
        Gson serializer = new Gson();
        UserData req = serializer.fromJson(ctx.body(), UserData.class);
        if(req.username() == null || req.password() == null || req.email() == null) {
            ctx.status(400);
            ctx.result("{\"message\": \"Error: bad request\"}");
        } else {
            try {
                AuthData res = UserService.registerUser(req);
                ctx.status(200);
                ctx.result(serializer.toJson(res));
            } catch (AlreadyTakenException e) {
                ctx.status(403);
                ctx.result("{\"message\": \"Error: already taken\"}");
            } catch (Exception e) {
                ctx.status(500);
                ctx.result("{\"message\": \"Error: something went wrong\"}");
            }
        }
    }

    private void login(Context ctx) {
        Gson serializer = new Gson();
        LoginRequest req = serializer.fromJson(ctx.body(), LoginRequest.class);
        if(req.username() == null || req.password() == null) {
            ctx.status(400);
            ctx.result("{\"message\": \"Error: bad request\"}");
        } else {
            try {
                AuthData res = UserService.loginUser(req);
                ctx.status(200);
                ctx.result(serializer.toJson(res));
            } catch (IncorrectUsernameOrPasswordException e) {
                ctx.status(401);
                ctx.result("{\"message\": \"Error: unauthorized\"}");
            } catch (Exception e) {
                ctx.status(500);
                ctx.result("{\"message\": \"Error: something went wrong\"}");
            }
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            AuthService.logoutUser(authToken);
            ctx.status(200);
            ctx.result("{}");
        } catch (UnrecognizedAuthTokenException e) {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: unauthorized\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: something went wrong\"}");
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}

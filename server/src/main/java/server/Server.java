package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataobjects.*;
import io.javalin.*;
import io.javalin.http.Context;

import service.*;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

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
            ws.onMessage(ctx -> {
                UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
                System.out.println("received " + cmd.getCommandType() + ", " + cmd.getAuthToken() + ", " + cmd.getGameID());
                ctx.send("{\"message\":\"" + ctx.message() + "\"}");
            });
            ws.onClose(ctx -> System.out.println("Websocket closed"));
        });
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
            Collection<GameData> gameList = GameService.listGames(authToken);
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

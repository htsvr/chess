package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataobjects.*;
import io.javalin.*;
import io.javalin.http.Context;

import services.*;

import java.util.Collection;
import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> {UserServices.clear(); AuthServices.clear(); GameServices.clear();});
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
    }

    private void joinGame(Context ctx) {
        String authToken = ctx.header("Authorization");
        Gson serializer = new Gson();
        try {
            JoinRequest req = serializer.fromJson(ctx.body(), JoinRequest.class);
            if(req.playerColor() == null || req.gameID() <= 0) {
                throw new Exception();
            }
            GameServices.joinGame(new JoinRequest(req.playerColor(), req.gameID(), authToken));
            ctx.status(200);
            ctx.result("{}");
        } catch (UnrecognizedAuthTokenException e) {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: unauthorized\"}");
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result("{\"message\": \"Error: already taken\"}");
        } catch (Exception e) {
            ctx.status(400);
            ctx.result("{\"message\": \"Error: bad request\"}");
        }
    }

    private void listGames(Context ctx) {
        String authToken = ctx.header("Authorization");
        Gson serializer = new Gson();
        try {
            Collection<GameData> gameList = GameServices.listGames(authToken);
            ctx.status(200);
            ctx.result("{\"games\": " + serializer.toJson(gameList) + "}");
        } catch (UnrecognizedAuthTokenException e) {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: unauthorized\"}");
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
                    int gameID = GameServices.createGame(body.get("gameName"), authToken);
                    ctx.status(200);
                    ctx.result("{\"gameID\": " + gameID + "}");
                } catch (UnrecognizedAuthTokenException e) {
                    ctx.status(401);
                    ctx.result("{\"message\": \"Error: unauthorized\"}");
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
                AuthData res = UserServices.registerUser(req);
                ctx.status(200);
                ctx.result(serializer.toJson(res));
            } catch (AlreadyTakenException e) {
                ctx.status(403);
                ctx.result("{\"message\": \"Error: already taken\"}");
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
                AuthData res = UserServices.loginUser(req);
                ctx.status(200);
                ctx.result(serializer.toJson(res));
            } catch (IncorrectUsernameOrPasswordException e) {
                ctx.status(401);
                ctx.result("{\"message\": \"Error: unauthorized\"}");
            }
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            AuthServices.logoutUser(authToken);
            ctx.status(200);
            ctx.result("{}");
        } catch (UnrecognizedAuthTokenException e) {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: unauthorized\"}");
        } catch (DataAccessException e) {
            ctx.status(500);
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

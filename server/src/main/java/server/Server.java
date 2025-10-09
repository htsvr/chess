package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataobjects.*;
import io.javalin.*;
import io.javalin.http.Context;
import services.AlreadyTakenException;
import services.IncorrectUsernameOrPasswordException;
import services.UnrecognizedAuthTokenException;

import static services.UserServices.*;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> clear());
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
    }

    private void register(Context ctx) {
        Gson serializer = new Gson();
        UserData req = serializer.fromJson(ctx.body(), UserData.class);
        if(req.username() == null || req.password() == null || req.email() == null) {
            ctx.status(400);
            ctx.result("{\"message\": \"Error: bad request\"}");
        } else {
            try {
                AuthData res = registerUser(req);
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
                AuthData res = loginUser(req);
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
            logoutUser(authToken);
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

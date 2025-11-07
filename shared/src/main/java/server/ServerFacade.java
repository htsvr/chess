package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataobjects.AuthData;
import dataobjects.GameData;
import dataobjects.LoginRequest;
import dataobjects.UserData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData registerUser(UserData user) throws ResponseException, IOException, InterruptedException {
        HttpResponse<String> res = request("/user", "POST", user, null);
        if (res.statusCode()/100 == 2) {
            return new Gson().fromJson(res.body(), AuthData.class);
        } else {
            throw new ResponseException(res);
        }
    }

    public AuthData login(LoginRequest req) throws ResponseException, IOException, InterruptedException {
        HttpResponse<String> res = request("/session", "POST", req, null);
        if (res.statusCode()/100 == 2){
            return new Gson().fromJson(res.body(), AuthData.class);
        } else {
            throw new ResponseException(res);
        }
    }

    public void logout(String authToken) throws ResponseException, IOException, InterruptedException {
        HttpResponse<String> res = request("/session", "DELETE", null, authToken);
        if (res.statusCode()/100 != 2) {
            throw new ResponseException(res);
        }
    }

    public void clear() throws IOException, InterruptedException, ResponseException {
        HttpResponse<String> res = request("/db", "DELETE", null, null);
        if (res.statusCode()/100 != 2){
            throw new ResponseException(res);
        }
    }

    public Collection<GameData> listGames(String authToken) throws IOException, InterruptedException, ResponseException {
        HttpResponse<String> res = request("/game", "GET", null, authToken);
        if (res.statusCode()/100 == 2){
            return new Gson().fromJson(res.body(), new TypeToken<ArrayList<GameData>>(){}.getType());
        } else {
            throw new ResponseException(res);
        }
    }

    public int createGame(String gameName, String authToken) throws IOException, InterruptedException, ResponseException {
        HttpResponse<String> res = request("/game", "GET", null, authToken);
        if (res.statusCode()/100 == 2){
            Map<String, Integer> result = new Gson().fromJson(res.body(), new TypeToken<Map<String, Integer>>(){}.getType());
            return result.get("gameName");
        } else {
            throw new ResponseException(res);
        }
    }

    private HttpResponse<String> request(String path, String method, Object body, String authToken) throws IOException, InterruptedException {
        HttpRequest.Builder req = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)));
        if (body != null) {
            req.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            req.setHeader("Authorization", authToken);
        }
        return client.send(req.build(), HttpResponse.BodyHandlers.ofString());
    }
}

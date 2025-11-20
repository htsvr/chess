package client;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataobjects.*;
import jakarta.websocket.*;
import ui.BoardHandler;
import ui.EscapeSequences;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ClientEndpoint
public class ServerFacade extends Endpoint {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final Session session;
    private final BoardHandler boardHandler;

    public ServerFacade(String url, BoardHandler boardHandler) throws URISyntaxException, DeploymentException, IOException {
        serverUrl = url;
        this.boardHandler = boardHandler;
        URI uri = new URI(url.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);
        this.session.addMessageHandler((MessageHandler.Whole<String>) this::evalMessage);
    }

    public void evalMessage(String messageJson) {
        ServerMessage message = new Gson().fromJson(messageJson, ServerMessage.class);
        switch(message.getServerMessageType()) {
            case NOTIFICATION -> {
                message = new Gson().fromJson(messageJson, NotificationServerMessage.class);
                System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW +
                        ((NotificationServerMessage) message).getMessage()
                        + EscapeSequences.RESET_TEXT_COLOR);
            }
            case ERROR -> {
                message = new Gson().fromJson(messageJson, ErrorServerMessage.class);
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: " +
                        ((ErrorServerMessage) message).getErrorMessage()
                        + EscapeSequences.RESET_TEXT_COLOR);
            }
            case LOAD_GAME -> {
                message = new Gson().fromJson(messageJson, LoadGameServerMessage.class);
                boardHandler.updateBoard(((LoadGameServerMessage) message).getGame().getBoard());
            }
        }
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

    public ArrayList<GameData> listGames(String authToken) throws IOException, InterruptedException, ResponseException {
        HttpResponse<String> res = request("/game", "GET", null, authToken);
        if (res.statusCode()/100 == 2){
            Map<String, ArrayList<GameData>> result = new Gson().fromJson(res.body(), new TypeToken<Map<String, ArrayList<GameData>>>(){}.getType());
            return result.get("games");
        } else {
            throw new ResponseException(res);
        }
    }

    public int createGame(String gameName, String authToken) throws IOException, InterruptedException, ResponseException {
        Map<String, String> body = new HashMap<>();
        body.put("gameName", gameName);
        HttpResponse<String> res = request("/game", "POST", body, authToken);
        if (res.statusCode()/100 == 2){
            Map<String, Integer> result = new Gson().fromJson(res.body(), new TypeToken<Map<String, Integer>>(){}.getType());
            return result.get("gameID");
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

    public void joinGame(JoinRequest req) throws IOException, InterruptedException, ResponseException {
        HttpResponse<String> res = request("/game", "PUT", req, req.authToken());
        if (res.statusCode()/100 != 2){
            throw new ResponseException(res);
        }
        session.getBasicRemote().sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.CONNECT, req.authToken(), req.gameID())));
    }

    public void observeGame(JoinRequest req) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.CONNECT, req.authToken(), req.gameID())));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID)));
    }

    public void resign(String authToken, int gameID) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID)));
    }

    public void move(String authToken, int gameID, ChessMove move) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move)));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connected");
    }
}

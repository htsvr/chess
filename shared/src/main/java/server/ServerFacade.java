package server;

import com.google.gson.Gson;
import dataobjects.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void registerUser(UserData user) throws Exception{
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user)))
                .setHeader("Content-Type", "application/json")
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
    }
}

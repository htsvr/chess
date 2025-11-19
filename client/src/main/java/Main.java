import chess.*;
import jakarta.websocket.DeploymentException;
import ui.ChessClient;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws DeploymentException, URISyntaxException, IOException {
        String serverUrl = "http://localhost:8080";
        ChessClient client = new ChessClient(serverUrl);
        client.run();
    }
}
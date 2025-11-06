package ui;
import dataobjects.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {
    private State state;
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        state = State.SIGNEDOUT;
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(" Welcome to chess. Sign in to start.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (result != "quit") {
            System.out.print("\n>>>");
            String line = scanner.nextLine();

            result = eval(line);
            System.out.println(result);
        }
    }

    public String eval(String line) {
        String[] tokens = line.toLowerCase().split(" ");
        String cmd = "help";
        String[] params = null;
        if (tokens.length > 0) {
            cmd = tokens[0];
            params = Arrays.copyOfRange(tokens, 1, tokens.length);
        }
        return switch (cmd){
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String register(String[] params) {
        if (params.length != 3) {
            return "please register using the form 'register [username] [email] [password]'";
        } else {
            UserData user = new UserData(params[0], params[2], params[1]);
            try{
                server.registerUser(user);
            } catch (Exception e){
                return "Something went wrong";
            }
        }
        return null;
    }

    public String login(String[] params) {
        return null;
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - help
                    - quit
                    - login
                    - register
                    """;
        } else {
            return """
                    - help
                    - logout
                    - create game
                    - list games
                    - play game
                    - observe game
                    """;
        }
    }
}

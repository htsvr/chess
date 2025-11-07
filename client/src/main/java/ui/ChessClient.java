package ui;
import dataobjects.*;
import server.ResponseException;
import server.ServerFacade;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {
    private State state;
    private final ServerFacade serverFacade;
    private AuthData auth;

    public ChessClient(String serverUrl) {
        state = State.SIGNEDOUT;
        serverFacade = new ServerFacade(serverUrl);
        auth = null;
    }

    public void run() {
        System.out.println(" Welcome to chess. Sign in to start.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
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
            case "logout" -> logout();
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
                auth = serverFacade.registerUser(user);
                if(auth != null) {
                    state = State.SIGNEDIN;
                    return "Registered user\n\n" + help();
                } else {
                    return "Something went wrong, please try again later";
                }
            } catch (IOException | InterruptedException e){
                return "Something went wrong with the connection, please try again later";
            } catch (ResponseException e) {
                if(e.statusCode() / 100 == 5) {
                    return "Something went wrong with the server, please try again later";
                } else if (e.statusCode() == 400) {
                    return "please register using the form 'register [username] [email] [password]'";
                } else if (e.statusCode() == 403) {
                    return "Username already in use, please pick a different username";
                } else {
                    return "Something went wrong";
                }
            }
        }
    }

    public String login(String[] params) {
        if (params.length != 2) {
            return "please login using the form 'login [username] [password]'";
        } else {
            LoginRequest user = new LoginRequest(params[0], params[1]);
            try{
                auth = serverFacade.login(user);
                if(auth != null) {
                    state = State.SIGNEDIN;
                    return "Logged in\n\n" + help();
                } else {
                    return "Something went wrong, please try again later";
                }
            } catch (IOException | InterruptedException e){
                return "Something went wrong with the connection, please try again later";
            } catch (ResponseException e) {
                if(e.statusCode() / 100 == 5) {
                    return "Something went wrong with the server, please try again later";
                } else if (e.statusCode() == 400) {
                    return "please register using the form 'register [username] [email] [password]'";
                } else if (e.statusCode() == 401) {
                    return "Incorrect username or password";
                } else {
                    return "Something went wrong";
                }
            }
        }
    }

    public String logout() {
        try {
            serverFacade.logout(auth.authToken());
            state = State.SIGNEDOUT;
            return "Successfully logged out" + help();
        } catch (IOException | InterruptedException e){
            return "Something went wrong with the connection, please try again later";
        } catch (ResponseException e) {
            if(e.statusCode() / 100 == 5) {
                return "Something went wrong with the server, please try again later";
            } else if (e.statusCode() == 401) {
                return "Invalid auth token";
            } else {
                return "Something went wrong";
            }
        }
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

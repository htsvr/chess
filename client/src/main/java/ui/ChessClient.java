package ui;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import dataobjects.*;
import server.ResponseException;
import server.ServerFacade;

import java.io.IOException;
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient {
    private State state;
    private final ServerFacade serverFacade;
    private AuthData auth;
    private Map<Integer, Integer> gameLookup;

    public ChessClient(String serverUrl) {
        state = State.SIGNEDOUT;
        serverFacade = new ServerFacade(serverUrl);
        auth = null;
        gameLookup = new HashMap<>();
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
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "observe" -> observeGame(params);
            case "join" -> joinGame(params);
            default -> help();
        };
    }

    public String register(String[] params) {
        if (params.length != 3) {
            return "Please register using the form 'register [username] [email] [password]'";
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
                    return "Please register using the form 'register [username] [email] [password]'";
                } else if (e.statusCode() == 403) {
                    return "Username already in use, please pick a different username";
                } else {
                    return "Something went wrong";
                }
            } catch (Exception e) {
                return "Something went wrong";
            }
        }
    }

    public String login(String[] params) {
        if (params.length != 2) {
            return "Please login using the form 'login [username] [password]'";
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
                    return "Please register using the form 'register [username] [email] [password]'";
                } else if (e.statusCode() == 401) {
                    return "Incorrect username or password";
                } else {
                    return "Something went wrong";
                }
            } catch (Exception e) {
                return "Something went wrong";
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
                return "You are not authorized to perform this action";
            } else {
                return "Something went wrong";
            }
        } catch (NullPointerException e) {
            return "You are not authorized to perform this action";
        } catch (Exception e) {
            return "Something went wrong";
        }
    }

    public String createGame(String[] params) {
        if (params.length != 1) {
            return "Please create a game using the form 'create [game name]'";
        }
        try {
            serverFacade.createGame(params[0], auth.authToken());
            return "Game created";
        } catch (IOException | InterruptedException e){
            return "Something went wrong with the connection, please try again later";
        } catch (ResponseException e) {
            if(e.statusCode() / 100 == 5) {
                return "Something went wrong with the server, please try again later";
            } else if (e.statusCode() == 401) {
                return "You are not authorized to perform this action";
            } else if (e.statusCode() == 400) {
                return "Please login using the form 'create [game name]'";
            } else {
                return "Something went wrong";
            }
        } catch (NullPointerException e) {
            return "You are not authorized to perform this action";
        } catch (Exception e) {
            return "Something went wrong";
        }
    }

    public String listGames() {
        try {
            ArrayList<GameData> gameList = serverFacade.listGames(auth.authToken());
            StringBuilder result = new StringBuilder();
            for (int i = 1; i <= gameList.size(); i ++) {
                result.append(i)
                        .append(" | ")
                        .append(gameList.get(i - 1).gameName())
                        .append(" | ")
                        .append(gameList.get(i - 1).whiteUsername())
                        .append(" | ")
                        .append(gameList.get(i - 1).blackUsername())
                        .append("\n");
                gameLookup.put(i, gameList.get(i-1).gameID());
            }
            return result.toString();
        } catch (IOException | InterruptedException e){
            return "Something went wrong with the connection, please try again later";
        } catch (ResponseException e) {
            if(e.statusCode() / 100 == 5) {
                return "Something went wrong with the server, please try again later";
            } else if (e.statusCode() == 401) {
                return "You are not authorized to perform this action";
            } else {
                return "Something went wrong";
            }
        } catch (NullPointerException e) {
            return "You are not authorized to perform this action";
        } catch (Exception e) {
            return "Something went wrong";
        }
    }

    public String joinGame(String[] params) {
        if (params.length != 2 || !params[0].matches("\\d+") || !(params[1].equals("white") || params[1].equals("black"))) {
            return "Please join a game using the form 'join [game number] [color]'";
        }
        try {
            ChessGame.TeamColor color = params[1].equals("white") ? ChessGame.TeamColor.WHITE: ChessGame.TeamColor.BLACK;
            int gameNumber = Integer.parseInt(params[0]);
            if(!gameLookup.containsKey(gameNumber)){
                return "Invalid game number";
            }
            int gameID = gameLookup.get(gameNumber);
            serverFacade.joinGame(new JoinRequest(color, gameID, auth.authToken()));
            return getGameString(gameID, color);
        } catch (IOException | InterruptedException e){
            return "Something went wrong with the connection, please try again later";
        } catch (ResponseException e) {
            if(e.statusCode() / 100 == 5) {
                return "Something went wrong with the server, please try again later";
            } else if (e.statusCode() == 401) {
                return "You are not authorized to perform this action";
            } else if (e.statusCode() == 400) {
                return "Please join a game using the form 'join [game number] [color]'";
            } else if (e.statusCode() == 403) {
                return "Already taken, please pick a different game and/or color";
            } else {
                return "Something went wrong";
            }
        } catch (NullPointerException e) {
            return "You are not authorized to perform this action";
        } catch (Exception e) {
            return "Something went wrong";
        }
    }

    public String observeGame(String[] params) {
        if (params.length != 1 || !params[0].matches("\\d+")) {
            return "Please observe a game using the form 'observe [game number]'";
        }
        try {
            int gameNumber = Integer.parseInt(params[0]);
            if(!gameLookup.containsKey(gameNumber)){
                return "Invalid game number";
            }
            int gameID = gameLookup.get(gameNumber);
            return getGameString(gameID, ChessGame.TeamColor.WHITE);
//        } catch (IOException | InterruptedException e){
//            return "Something went wrong with the connection, please try again later";
//        } catch (ResponseException e) {
//            if(e.statusCode() / 100 == 5) {
//                return "Something went wrong with the server, please try again later";
//            } else if (e.statusCode() == 401) {
//                return "You are not authorized to perform this action";
//            } else if (e.statusCode() == 400) {
//                return "Please observe a game using the form 'observe [game number]'";
//            } else {
//                return "Something went wrong";
//            }
        } catch (NullPointerException e) {
            return "You are not authorized to perform this action";
        } catch (Exception e) {
            return "Something went wrong";
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
                    - create
                    - list
                    - play
                    - observe
                    """;
        }
    }

    private String getGameString(int gameID, ChessGame.TeamColor color) throws IOException, InterruptedException, ResponseException{
        ArrayList<GameData> gameList = serverFacade.listGames(auth.authToken());
        ChessGame resultGame = null;
        for (GameData game: gameList) {
            if(game.gameID() == gameID) {
                resultGame = game.game();
            }
        }
        return getBoardString(resultGame.getBoard());
    }

    private String getBoardString(ChessBoard board) {
        StringBuilder result = new StringBuilder();
        for (int r = 7; r >= 0; r--){
            result.append(SET_TEXT_COLOR_BLACK).
                    append(SET_BG_COLOR_LIGHT_GREY)
                    .append(r+1);
            for (int c = 0; c < 8; c++) {
                var piece = board.getPiece(new ChessPosition(r + 1, c + 1));
                if (piece == null){
                    result.append(" |");
                } else {
                    result.append(piece).append("|");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }
}

package ui;
import chess.*;
import dataobjects.*;
import client.ResponseException;
import client.ServerFacade;
import jakarta.websocket.DeploymentException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class ChessClient {
    private State state;
    private final ServerFacade serverFacade;
    private AuthData auth;
    private final Map<Integer, Integer> gameLookup;
    private int currentGameID;
    private final BoardHandler boardHandler;

    public ChessClient(String serverUrl) throws DeploymentException, URISyntaxException, IOException {
        state = State.SIGNED_OUT;
        boardHandler = new BoardHandler();
        serverFacade = new ServerFacade(serverUrl, boardHandler);
        auth = null;
        gameLookup = new HashMap<>();
    }

    public void run() {
        System.out.println("♕ 240 Chess Client: ");
        System.out.println(" Welcome to chess. Sign in to start.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            if(state != State.GAMEPLAY) {
                System.out.print("[" + state + "] >>>");
            }
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
        if (state == State.SIGNED_OUT) {
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } else if (state == State.SIGNED_IN) {
            return switch (cmd) {
                case "logout" -> logout();
                case "quit" -> "quit";
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "observe" -> observeGame(params);
                case "join" -> joinGame(params);
                default -> help();
            };
        } else {
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                default -> help();
            };
        }
    }

    public String redraw() {
        boardHandler.drawBoard();
        return "";
    }

    public String leave() {
        try {
            serverFacade.leaveGame(auth.authToken(), currentGameID);
            state = State.SIGNED_IN;
            return "";
        } catch (Exception e) {
            return handleErrors(e, null);
        }
    }

    public String move(String[] params) {
        String incorrectFormMessage = "Please move using the form 'move <START><END>=<PROMOTION PIECE>' i.e. 'move e2e3' or 'move b7b8=Q";
        if (!(params.length == 1 &&
                ('a' <= params[0].charAt(0) && params[0].charAt(0) <= 'h') &&
                ('1' <= params[0].charAt(1) && params[0].charAt(1) <= '8') &&
                ('a' <= params[0].charAt(2) && params[0].charAt(2) <= 'h') &&
                ('1' <= params[0].charAt(3) && params[0].charAt(3) <= '8') &&
                (params[0].length() == 4 || (params[0].length() == 6 &&
                        params[0].charAt(4) == '=')))) {
            return incorrectFormMessage;
        }
        ChessPosition start = new ChessPosition(((int) params[0].charAt(1))-48, ((int) params[0].charAt(0))-96);
        ChessPosition end = new ChessPosition(((int) params[0].charAt(3))-48, ((int) params[0].charAt(2))-96);
        ChessMove move;
        if(params[0].length() == 4) {
            move = new ChessMove(start, end, null);
        } else {
            ChessPiece.PieceType piece = null;
            switch(params[0].charAt(5)){
                case 'q' -> piece = ChessPiece.PieceType.QUEEN;
                case 'n' -> piece = ChessPiece.PieceType.KNIGHT;
                case 'b' -> piece = ChessPiece.PieceType.BISHOP;
                case 'r' -> piece = ChessPiece.PieceType.ROOK;
                case 'p' -> piece = ChessPiece.PieceType.PAWN;
            }
            if (piece == null) {
                return incorrectFormMessage;
            }
            move = new ChessMove(start, end, piece);
        }
        try {
            serverFacade.move(auth.authToken(), currentGameID, move);
            return "";
        } catch (Exception e) {
            return handleErrors(e, null);
        }
    }

    public String resign() {
        try {
            serverFacade.resign(auth.authToken(), currentGameID);
            return "";
        } catch (Exception e) {
            return handleErrors(e, null);
        }
    }

    public String highlight(String[] params) {
        String incorrectFormMessage = "Please highlight using the form 'highlight <PIECE POSITION>' i.e. 'highlight e2'";
        if (params.length != 1 ||
                !('a' <= params[0].charAt(0) && params[0].charAt(0) <= 'h') ||
                !('1' <= params[0].charAt(1) && params[0].charAt(1) <= '8')) {
            return incorrectFormMessage;
        }
        ChessPosition pos = new ChessPosition(((int) params[0].charAt(1))-48, ((int) params[0].charAt(0))-96);
        boardHandler.highlightBoard(pos);
        return "";
    }

    public String register(String[] params) {
        String incorrectFormMessage = "Please register using the form 'register <USERNAME> <EMAIL> <PASSWORD>'";
        if (params.length != 3) {
            return incorrectFormMessage;
        } else {
            UserData user = new UserData(params[0], params[2], params[1]);
            try{
                auth = serverFacade.registerUser(user);
                if(auth != null) {
                    state = State.SIGNED_IN;
                    return "Registered user " + auth.username();
                } else {
                    return "Something went wrong, please try again later";
                }
            } catch (Exception e) {
                return handleErrors(e, Map.of(400, incorrectFormMessage, 403, "Username already in use, please pick a different username"));
            }
        }
    }

    public String login(String[] params) {
        String incorrectFormMessage = "Please login using the form 'login <USERNAME> <PASSWORD>'";
        if (params.length != 2) {
            return incorrectFormMessage;
        } else {
            LoginRequest user = new LoginRequest(params[0], params[1]);
            try{
                auth = serverFacade.login(user);
                if(auth != null) {
                    state = State.SIGNED_IN;
                    return "Logged in as " + auth.username();
                } else {
                    return "Something went wrong, please try again later";
                }
            } catch (Exception e) {
                return handleErrors(e, Map.of(400, incorrectFormMessage, 401, "Incorrect username or password"));
            }
        }
    }

    public String logout() {
        try {
            serverFacade.logout(auth.authToken());
            state = State.SIGNED_OUT;
            return "Successfully logged out";
        } catch (Exception e) {
            return handleErrors(e, null);
        }
    }

    public String createGame(String[] params) {
        String incorrectFormMessage = "Please create a game using the form 'create <GAME NAME>'";
        if (params.length != 1) {
            return incorrectFormMessage;
        }
        try {
            serverFacade.createGame(params[0], auth.authToken());
            return "Game created";
        } catch (Exception e) {
            return handleErrors(e, Map.of(400, incorrectFormMessage));
        }
    }

    public String listGames() {
        try {
            ArrayList<GameData> gameList = serverFacade.listGames(auth.authToken());
            StringBuilder result = new StringBuilder();
            result.append(String.format("%-5s%-20s%-20s%-20s", "id", "game name", "white player", "black player"))
                    .append("\n")
                    .append("-".repeat(65))
                    .append("\n");
            for (int i = 1; i <= gameList.size(); i ++) {
                GameData game = gameList.get(i - 1);
                String name = game.gameName();
                String whiteUsername = game.whiteUsername() == null ? "": game.whiteUsername();
                String blackUsername = game.blackUsername() == null ? "": game.blackUsername();
                result.append(String.format("%-5d%-20s%-20s%-20s", i, name, whiteUsername, blackUsername))
                        .append("\n");
                gameLookup.put(i, gameList.get(i-1).gameID());
            }
            return result.toString();
        } catch (Exception e) {
            return handleErrors(e, null);
        }
    }

    public String joinGame(String[] params) {
        String incorrectFormMessage = "Please join a game using the form 'join <GAME ID> [WHITE|BLACK]'";
        if (params.length != 2 || !params[0].matches("\\d+") || !(params[1].equals("white") || params[1].equals("black"))) {
            return incorrectFormMessage;
        }
        try {
            boardHandler.setColor(params[1].equals("white") ? ChessGame.TeamColor.WHITE: ChessGame.TeamColor.BLACK);
            int gameNumber = Integer.parseInt(params[0]);
            if(!gameLookup.containsKey(gameNumber)){
                return "Invalid game id";
            }
            currentGameID = gameLookup.get(gameNumber);
            serverFacade.joinGame(new JoinRequest(boardHandler.getColor(), currentGameID, auth.authToken()));
            state = State.GAMEPLAY;
            return "";
        }  catch (Exception e) {
            return handleErrors(e, Map.of(400, incorrectFormMessage, 403, "Already taken, please pick a different game and/or color"));
        }
    }

    public String observeGame(String[] params) {
        String incorrectFormMessage = "Please observe a game using the form 'observe <ID>'";
        if (params.length != 1 || !params[0].matches("\\d+")) {
            return incorrectFormMessage;
        }
        try {
            int gameNumber = Integer.parseInt(params[0]);
            if(!gameLookup.containsKey(gameNumber)){
                return "Invalid game id";
            }
            currentGameID = gameLookup.get(gameNumber);
            serverFacade.observeGame(new JoinRequest(ChessGame.TeamColor.WHITE, currentGameID, auth.authToken()));
            state = State.GAMEPLAY;
            return "";
        } catch (Exception e) {
            return handleErrors(e, Map.of(400, incorrectFormMessage));
        }
    }

    public String help() {
        if (state == State.SIGNED_OUT) {
            return """
                    - help
                    - quit
                    - login <USERNAME> <PASSWORD>
                    - register <USERNAME> <EMAIL> <PASSWORD>
                    """;
        } else if (state == State.SIGNED_IN) {
            return """
                    - help
                    - logout
                    - create <GAME NAME>
                    - list
                    - join <GAME ID> [WHITE|BLACK]
                    - observe <ID>
                    """;
        } else {
            return """
                    - help
                    - redraw
                    - leave
                    - move
                    - resign
                    - highlight
                    """;
        }
    }

    private String handleErrors(Exception e, Map<Integer, String> specialErrorMessages) {
        if (e.getClass() == ResponseException.class) {
            if (specialErrorMessages.containsKey(((ResponseException) e).statusCode())) {
                return specialErrorMessages.get(((ResponseException) e).statusCode());
            } else if(((ResponseException) e).statusCode() / 100 == 5) {
                return "Something went wrong with the server, please try again later";
            } else if (((ResponseException) e).statusCode() == 401) {
                return "You are not authorized to perform this action";
            } else if (((ResponseException) e).statusCode() == 400) {
                return "Invalid command form";
            } else if (((ResponseException) e).statusCode() == 403) {
                return "Already taken";
            } else {
                return "Something went wrong";
            }
        } else if (e.getClass() == IOException.class || e.getClass() == InterruptedException.class) {
            return "Something went wrong with the connection, please try again later";
        } else if (e.getClass() == NullPointerException.class) {
            return "You are not authorized to perform this action";
        } else {
            return "Something went wrong";
        }
    }
}

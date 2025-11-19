package ui;
import chess.*;
import dataobjects.*;
import client.ResponseException;
import client.ServerFacade;
import jakarta.websocket.DeploymentException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient {
    private State state;
    private final ServerFacade serverFacade;
    private AuthData auth;
    private final Map<Integer, Integer> gameLookup;
    private int currentGameID;
    private ChessBoard board;
    private ChessGame.TeamColor color;

    public ChessClient(String serverUrl) throws DeploymentException, URISyntaxException, IOException {
        state = State.SIGNED_OUT;
        serverFacade = new ServerFacade(serverUrl);
        auth = null;
        gameLookup = new HashMap<>();
    }

    public void run() throws IOException {
        System.out.println("♕ 240 Chess Client: ");
        System.out.println(" Welcome to chess. Sign in to start.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            System.out.print("[" + state + "] >>>");
            String line = scanner.nextLine();

            result = eval(line);
            System.out.println(result);
        }
    }

    public String eval(String line) throws IOException {
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
                case "echo" -> echo(params);
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
                case "echo" -> echo(params);
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
        return getBoardString(board, color);
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
        ChessMove move = new ChessMove(new ChessPosition(1, 2), new ChessPosition(3, 4), null);
        try {
            serverFacade.move(auth.authToken(), currentGameID, move);
            return "";
        } catch (Exception e) {
            return handleErrors(e, null);
        }
    }

    public String resign() {
        try {
            serverFacade.leaveGame(auth.authToken(), currentGameID);
            return "";
        } catch (Exception e) {
            return handleErrors(e, null);
        }
    }

    public String highlight(String[] params) {
        return "";
    }

    public String echo(String[] params) {
        try {
            serverFacade.echo(params[0]);
            return "message sent";
        } catch (Exception e) {
            return "Something went wrong";
        }
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
            color = params[1].equals("white") ? ChessGame.TeamColor.WHITE: ChessGame.TeamColor.BLACK;
            int gameNumber = Integer.parseInt(params[0]);
            if(!gameLookup.containsKey(gameNumber)){
                return "Invalid game id";
            }
            currentGameID = gameLookup.get(gameNumber);
            serverFacade.joinGame(new JoinRequest(color, currentGameID, auth.authToken()));
            state = State.GAMEPLAY;
            return getGameString(currentGameID, color);
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
            return getGameString(currentGameID, ChessGame.TeamColor.WHITE);
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

    private String getGameString(int gameID, ChessGame.TeamColor color) throws IOException, InterruptedException, ResponseException{
        ArrayList<GameData> gameList = serverFacade.listGames(auth.authToken());
        ChessGame resultGame = null;
        for (GameData game: gameList) {
            if(game.gameID() == gameID) {
                resultGame = game.game();
            }
        }
        if(resultGame != null) {
            return getBoardString(resultGame.getBoard(), color);
        }
        return "Could not retrieve game";
    }

    public String getBoardString(ChessBoard board, ChessGame.TeamColor teamColor) {
        StringBuilder result = new StringBuilder();
        boolean whiteTile;
        int[] rows, cols;
        if (teamColor == ChessGame.TeamColor.WHITE){
            rows = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
            cols = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        } else {
            cols = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
            rows = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        }

        String labelRow = "   \u2003a \u2003b \u2003c \u2003d \u2003e \u2003f \u2003g \u2003h    ";
        if(teamColor == ChessGame.TeamColor.BLACK){
            labelRow = new StringBuilder(labelRow).reverse().toString();
        }
        result.append(RESET_TEXT_BOLD_FAINT)
                .append(SET_TEXT_COLOR_LIGHT_GREY)
                .append(SET_BG_COLOR_DARK_GREY)
                .append(labelRow)
                .append(RESET_BG_COLOR)
                .append(RESET_TEXT_COLOR)
                .append("\n");
        for (int r: rows){
            result.append(SET_TEXT_COLOR_LIGHT_GREY)
                    .append(SET_BG_COLOR_DARK_GREY)
                    .append(" ")
                    .append(r+1)
                    .append(" ");
            for (int c: cols) {
                whiteTile = (r%2 + c%2)%2 == 1;
                result.append(whiteTile ? SET_BG_COLOR_WHITE : SET_BG_COLOR_LIGHT_GREY);
                ChessPiece piece = board.getPiece(new ChessPosition(r + 1, c + 1));
                if (piece == null){
                    result.append(EMPTY);
                } else {
                    if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                        result.append(SET_TEXT_COLOR_BLACK);
                    } else {
                        result.append(SET_TEXT_COLOR_DARK_GREY);
                    }
                    switch(piece.getPieceType()){
                        case ChessPiece.PieceType.PAWN ->
                            result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN: BLACK_PAWN);
                        case ChessPiece.PieceType.KING ->
                            result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING: BLACK_KING);
                        case ChessPiece.PieceType.QUEEN ->
                            result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN: BLACK_QUEEN);
                        case ChessPiece.PieceType.BISHOP ->
                            result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP: BLACK_BISHOP);
                        case ChessPiece.PieceType.KNIGHT ->
                            result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT: BLACK_KNIGHT);
                        case ChessPiece.PieceType.ROOK ->
                            result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK: BLACK_ROOK);
                    }
                }
            }
            result.append(SET_TEXT_COLOR_LIGHT_GREY).
                    append(SET_BG_COLOR_DARK_GREY)
                    .append(" ")
                    .append(r+1)
                    .append(" ")
                    .append(RESET_BG_COLOR)
                    .append(RESET_TEXT_COLOR)
                    .append("\n");
        }
        result.append(SET_TEXT_COLOR_LIGHT_GREY)
                .append(SET_BG_COLOR_DARK_GREY)
                .append(labelRow)
                .append(RESET_BG_COLOR)
                .append(RESET_TEXT_COLOR)
                .append("\n");
        return result.toString();
    }
}

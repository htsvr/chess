import chess.*;
import ui.ChessClient;
import ui.EscapeSequences;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        ChessClient client = new ChessClient(serverUrl);


        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        var piece2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        var board = new ChessBoard();
        var board2 = new ChessBoard();
        var pos = new ChessPosition(1, 6);
        System.out.println("♕ 240 Chess Client: ");
        System.out.println(board);
        board.resetBoard();
        board2.resetBoard();
        System.out.println(client.getBoardString(board, ChessGame.TeamColor.WHITE));
        System.out.println(client.getBoardString(board, ChessGame.TeamColor.BLACK));
        System.out.println(board2);
        System.out.println(board.equals(board2));
        client.run();
    }
}
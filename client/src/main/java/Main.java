import chess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        var board = new ChessBoard();
        System.out.println("♕ 240 Chess Client: ");
        System.out.println(board);
        board.resetBoard();
        System.out.println(board);
    }
}
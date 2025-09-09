import chess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        var board = new ChessBoard();
        var pos = new ChessPosition(3, 5);
        board.addPiece(pos, piece);
        System.out.println(board);
    }
}
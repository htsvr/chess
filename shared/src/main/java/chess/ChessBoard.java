package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return(board[position.getRow()-1][position.getColumn()-1]);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(int r = 0; r < board.length; r++){
            for (int c = 0; c < board[0].length; c++){
                board[r][c] = null;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
//        return Arrays.deepEquals(board, that.board);
        boolean arraysAreSame = true;
        for(int r = 0; r < board.length; r++){
            for (int c = 0; c < board.length; c++) {
                if (board[r][c] != that.board[r][c]){
                    arraysAreSame = false;
                }
            }
        }
        return arraysAreSame;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String result = "";
        for (int r = 0; r < 8; r++){
            result += "|";
            for (int c = 0; c < 8; c++) {
                var piece = getPiece(new ChessPosition(r + 1, c + 1));
                if (piece == null){
                    result += " |";
                } else {
                    result += piece.toString() + "|";
                }
            }
            result += "\n";
        }
        return result;
    }
}

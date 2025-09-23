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
        for(int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                board[r][c] = null;
            }
        }
        // row 1
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        // row 2 and 7
        for (var c = 1; c < 9; c++){
            addPiece(new ChessPosition(2, c), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, c), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        // row 8
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
//        return Arrays.deepEquals(board, that.board);
        for(int r = 0; r < board.length; r++){
            for (int c = 0; c < board.length; c++) {
                if(board[r][c] == null){
                    if (that.board[r][c] != null){
                        return false;
                    }
                }
                else if (!(board[r][c].equals(that.board[r][c]))){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String result = "";
        for (int r = 7; r >= 0; r--){
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

    public ChessBoard copy() {
        ChessBoard boardCopy = new ChessBoard();
        for (int r = 1; r < 9; r ++){
            for (int c = 1; c < 9; c ++) {
                ChessPosition pos = new ChessPosition(r, c);
                if(getPiece(pos) != null) {
                    boardCopy.addPiece(pos, new ChessPiece(getPiece(pos).getTeamColor(), getPiece(pos).getPieceType()));
                }
            }
        }
        return boardCopy;
    }

    public boolean movePiece(ChessMove move) {
        if(getPiece(move.getStartPosition()) == null) {
            return false;
        }
        if(move.getPromotionPiece() == null) {
            addPiece(move.getEndPosition(), getPiece(move.getStartPosition()));
        } else {
            addPiece(move.getEndPosition(), new ChessPiece(getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece()));
        }
        board[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;
        return true;
    }
}

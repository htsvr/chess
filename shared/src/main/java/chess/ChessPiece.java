package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    @Override
    public String toString() {
        String result = "";
        switch (type) {
            case ChessPiece.PieceType.BISHOP:
                result = "b";
                break;
            case ChessPiece.PieceType.KING:
                result = "k";
                break;
            case ChessPiece.PieceType.KNIGHT:
                result = "n";
                break;
            case ChessPiece.PieceType.PAWN:
                result = "p";
                break;
            case ChessPiece.PieceType.QUEEN:
                result = "q";
                break;
            case ChessPiece.PieceType.ROOK:
                result = "r";
                break;
            default:
        }
        if (color == ChessGame.TeamColor.WHITE){
            result = result.toUpperCase();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(color);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        ChessPiece piece;
        if(type == PieceType.ROOK){
            for(int r = myPosition.getRow() + 1; r < 9; r++){
                piece = board.getPiece(new ChessPosition(r, myPosition.getColumn()));
                if(piece == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(r, myPosition.getColumn()), null));
                }
                else {
                    if (!piece.getTeamColor().equals(color)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(r, myPosition.getColumn()), null));
                    }
                    break;
                }
            }
            for(int r = myPosition.getRow() - 1; r > 0; r--){
                piece = board.getPiece(new ChessPosition(r, myPosition.getColumn()));
                if(piece == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(r, myPosition.getColumn()), null));
                }
                else {
                    if (!piece.getTeamColor().equals(color)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(r, myPosition.getColumn()), null));
                    }
                    break;
                }
            }
            for(int c = myPosition.getColumn() + 1; c < 9; c++){
                piece = board.getPiece(new ChessPosition(myPosition.getRow(), c));
                if(piece == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), c), null));
                }
                else {
                    if (!piece.getTeamColor().equals(color)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), c), null));
                    }
                    break;
                }
            }
            for(int c = myPosition.getColumn() - 1; c > 0; c--){
                piece = board.getPiece(new ChessPosition(myPosition.getRow(), c));
                if(piece == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), c), null));
                }
                else {
                    if (!piece.getTeamColor().equals(color)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), c), null));
                    }
                    break;
                }
            }
        }
        return moves;
    }
}

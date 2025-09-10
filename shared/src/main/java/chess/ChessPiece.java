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
        if (color == ChessGame.TeamColor.WHITE) {
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
        switch (type) {
            case PieceType.ROOK:
                return getRookMoves(board, myPosition);
            case PieceType.BISHOP:
                return getBishopMoves(board, myPosition);
            case PieceType.QUEEN:
                return getQueenMoves(board, myPosition);
            case PieceType.PAWN:
                return getPawnMoves(board, myPosition);
            case PieceType.KING:
                return getKingMoves(board, myPosition);
            default:
                return null;
        }
    }

    public boolean addMove(ChessBoard board, ChessPosition myPosition, ChessPosition movePosition, Collection<ChessMove> moves){
        if(!movePosition.inBounds(8)){
            return false;
        }
        ChessPiece piece = board.getPiece(movePosition);
        if (piece == null) {
            moves.add(new ChessMove(myPosition, movePosition, null));
            return true;
        } else {
            if (!piece.getTeamColor().equals(color)) {
                moves.add(new ChessMove(myPosition, movePosition, null));
            }
            return false;
        }
    }

    public Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        ChessPosition pos = myPosition;
        int[][] options = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] a: options){
            pos = myPosition;
            do {
                pos = new ChessPosition(pos.getRow()+a[0], pos.getColumn()+a[1]);
            } while (addMove(board, myPosition, pos, moves));
        }
        return moves;
    }

    public Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        ChessPosition pos = myPosition;
        int[][] options = {{-1, 1}, {-1, -1}, {1, -1}, {1, 1}};
        for (int[] a: options){
            pos = myPosition;
            do {
                pos = new ChessPosition(pos.getRow()+a[0], pos.getColumn()+a[1]);
            } while (addMove(board, myPosition, pos, moves));
        }
        return moves;
    }

    public Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = getBishopMoves(board, myPosition);
        moves.addAll(getRookMoves(board, myPosition));
        return moves;
    }

    public void pawnAdd(ChessPosition myPosition, ChessPosition movePosition, Collection<ChessMove> moves){
        if(movePosition.getRow() == 1 || movePosition.getRow() == 8){
            for (var promotionPiece : PieceType.values()){
                if(promotionPiece != PieceType.PAWN && promotionPiece != PieceType.KING) {
                    moves.add(new ChessMove(myPosition, movePosition, promotionPiece));
                }
            }
        }
        else {
            moves.add(new ChessMove(myPosition, movePosition, null));
        }
    }

    public Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        int dir = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        ChessPosition pos = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn());
        if(pos.inBounds(8) && board.getPiece(pos) == null){
            pawnAdd(myPosition, pos, moves);
            if (myPosition.getRow() == (dir == 1? 2 : 7)){
                pos = new ChessPosition(myPosition.getRow() + dir*2, myPosition.getColumn());
                if(pos.inBounds(8) && board.getPiece(pos) == null) {
                    pawnAdd(myPosition, pos, moves);
                }
            }
        }
        pos = new ChessPosition(myPosition.getRow()+dir, myPosition.getColumn()+1);
        if(pos.inBounds(8) && board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() != color){
            pawnAdd(myPosition, pos, moves);
        }
        pos = new ChessPosition(myPosition.getRow()+dir, myPosition.getColumn()-1);
        if(pos.inBounds(8) && board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() != color){
            pawnAdd(myPosition, pos, moves);
        }
        return moves;
    }

    public Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        ChessPosition pos = myPosition;
        int[][] options = {{-1, 1}, {-1, -1}, {1, -1}, {1, 1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        for (int[] a: options){
            pos = new ChessPosition(myPosition.getRow()+a[0], myPosition.getColumn()+a[1]);
            addMove(board, myPosition, pos, moves);
        }
        return moves;
    }
}
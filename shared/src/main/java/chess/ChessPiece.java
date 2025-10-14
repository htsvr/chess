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

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private int numberOfMovesMade;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        numberOfMovesMade = 0;
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
        if (pieceColor == ChessGame.TeamColor.WHITE) {
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
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(pieceColor);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public int getNumberOfMovesMade() {
        return numberOfMovesMade;
    }

    public void makeMove(){
        numberOfMovesMade ++;
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
            case PieceType.KING -> {
                int[][] dirs = {{-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {0, 1}, {1, 0}, {1, -1}, {1, 1}};
                return getMovesInDirections(board, myPosition, dirs, false);
            }
            case PieceType.QUEEN -> {
                int[][] dirs = {{-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {0, 1}, {1, 0}, {1, -1}, {1, 1}};
                return getMovesInDirections(board, myPosition, dirs, true);
            }
            case PieceType.BISHOP -> {
                int[][] dirs = {{-1, 1}, {-1, -1}, {1, -1}, {1, 1}};
                return getMovesInDirections(board, myPosition, dirs, true);
            }
            case PieceType.ROOK -> {
                int[][] dirs = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};
                return getMovesInDirections(board, myPosition, dirs, true);
            }
            case PieceType.KNIGHT -> {
                int[][] dirs = {{-1, 2}, {1, 2}, {2, -1}, {2, 1}, {-2, 1}, {1, -2}, {-2, -1}, {-1, -2}};
                return getMovesInDirections(board, myPosition, dirs, false);
            }
            case PieceType.PAWN -> {
                return getPawnMoves(board, myPosition);
            }
        }

        return new HashSet<ChessMove>();
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        int dir;
        if(pieceColor == ChessGame.TeamColor.WHITE){
            dir = 1;
        } else {
            dir = -1;
        }
        ChessPosition pos = new ChessPosition(myPosition.getRow()+dir, myPosition.getColumn());
        if(pos.inBounds(8)){
            if(board.getPiece(pos) == null){
                moves.addAll(checkPawnPromotion(myPosition, pos));
                if ((myPosition.getRow() == 2 && pieceColor == ChessGame.TeamColor.WHITE) || (myPosition.getRow() == 7 && pieceColor == ChessGame.TeamColor.BLACK)) {
                    pos = new ChessPosition(myPosition.getRow()+2*dir, myPosition.getColumn());
                    if(board.getPiece(pos) == null) {
                        moves.add(new ChessMove(myPosition, pos, null));
                    }
                }
            }
            pos = new ChessPosition(myPosition.getRow()+dir, myPosition.getColumn()+dir);
            if(pos.inBounds(8) && board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() != pieceColor) {
                moves.addAll(checkPawnPromotion(myPosition, pos));
            }
            pos = new ChessPosition(myPosition.getRow()+dir, myPosition.getColumn()-dir);
            if(pos.inBounds(8) && board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() != pieceColor) {
                moves.addAll(checkPawnPromotion(myPosition, pos));
            }
        }
        return moves;
    }

    private Collection<ChessMove> checkPawnPromotion(ChessPosition startPosition, ChessPosition endPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        if((endPosition.getRow() == 1 && pieceColor == ChessGame.TeamColor.BLACK) || (endPosition.getRow() == 8 && pieceColor == ChessGame.TeamColor.WHITE)) {
            for (PieceType promotionPiece:PieceType.values()) {
                if(promotionPiece != PieceType.PAWN && promotionPiece != PieceType.KING) {
                    moves.add(new ChessMove(startPosition, endPosition, promotionPiece));
                }
            }
        } else {
            moves.add(new ChessMove(startPosition, endPosition, null));
        }
        return moves;
    }

    private Collection<ChessMove> getMovesInDirection(ChessBoard board, ChessPosition startPosition, ChessPosition basePosition, int[] dir, boolean keepGoing) {
        Collection<ChessMove> moves = new HashSet<>();
        ChessPosition pos = new ChessPosition(basePosition.getRow()+dir[0], basePosition.getColumn()+dir[1]);
        if(pos.inBounds(8)){
            if(board.getPiece(pos) == null){
                moves.add(new ChessMove(startPosition, pos, null));
                if(keepGoing){
                    moves.addAll(getMovesInDirection(board, startPosition, pos, dir, true));
                }
            } else if (board.getPiece((pos)).getTeamColor() != pieceColor){
                moves.add(new ChessMove(startPosition, pos, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> getMovesInDirections(ChessBoard board, ChessPosition startPosition, int[][] dirs, boolean keepGoing) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        for (int[] dir:dirs) {
            moves.addAll(getMovesInDirection(board, startPosition, startPosition, dir, keepGoing));
        }
        return moves;
    }
}
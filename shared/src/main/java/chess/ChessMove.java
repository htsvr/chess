package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessMove chessMove = (ChessMove) o;
        boolean equalStartPosition = Objects.equals(getStartPosition(), chessMove.getStartPosition());
        boolean equalEndPosition = Objects.equals(getEndPosition(), chessMove.getEndPosition());
        boolean equalPromotionPiece = getPromotionPiece() == chessMove.getPromotionPiece();
        return equalStartPosition && equalEndPosition && equalPromotionPiece;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getStartPosition());
        result = 31 * result + Objects.hashCode(getEndPosition());
        result = 31 * result + Objects.hashCode(getPromotionPiece());
        return result;
    }

    @Override
    public String toString() {
        if(promotionPiece == null) {
            return startPosition.toString() + endPosition;
        }
        else{
            return startPosition.toString() + endPosition + "=" + new ChessPiece(ChessGame.TeamColor.WHITE, promotionPiece);
        }
    }
}

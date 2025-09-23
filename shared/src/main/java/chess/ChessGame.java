package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private TeamColor turnColor;

    public ChessGame() {
        // set up new chess board at set it to white's turn
        board.resetBoard();
        turnColor = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(board.getPiece(startPosition) != null && board.getPiece(startPosition).getTeamColor() == turnColor) { //Check if space is a piece of the right color
            Collection<ChessMove> all_moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
            Collection<ChessMove> valid_moves = new HashSet<>();
            for(ChessMove move:all_moves) {
                ChessBoard boardAfterMove = board.copy();
                if(boardAfterMove.movePiece(move)) {
                    if (wouldBeInCheck(boardAfterMove, turnColor)) {
                        continue;
                    }
                    valid_moves.add(move);
                }
            }
            return valid_moves;
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return wouldBeInCheck(board, teamColor);
    }

    /**
     * 
     * @param boardToCheck the board to look through
     * @return a collection of every ChessPosition with a ChessPiece in it
     */
    private static Collection<ChessPosition> getChessPositions(ChessBoard boardToCheck){
        Collection<ChessPosition> positions = new HashSet<>();
        for(int row = 1; row < 9; row ++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                if(boardToCheck.getPiece(pos) != null) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    /**
     *
     * @param boardToCheck ChessBoard that should be searched to see if the king is in check
     * @param teamColor which team to check for check
     * @return True if the specified team is in check on the board provided
     */
    private static boolean wouldBeInCheck(ChessBoard boardToCheck, TeamColor teamColor) {
        for(ChessPosition pos:getChessPositions(boardToCheck)){
            if(boardToCheck.getPiece(pos).getTeamColor() != teamColor) { //check if piece exists and is on the other team
                Collection<ChessMove> moves = boardToCheck.getPiece(pos).pieceMoves(boardToCheck, pos);
                for (ChessMove move:moves) {
                    if(boardToCheck.getPiece(move.getEndPosition()) != null && boardToCheck.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING && boardToCheck.getPiece(move.getEndPosition()).getTeamColor() == teamColor){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board.copy();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessGame chessGame = (ChessGame) o;
        return board.equals(chessGame.board) && turnColor == chessGame.turnColor;
    }

    @Override
    public int hashCode() {
        int result = board.hashCode();
        result = 31 * result + Objects.hashCode(turnColor);
        return result;
    }
}

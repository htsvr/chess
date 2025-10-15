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
    private ChessMove lastMove;

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
        if(board.getPiece(startPosition) != null) { //Check if space is a piece
            Collection<ChessMove> allMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
            Collection<ChessMove> validMoves = new HashSet<>();
            for(ChessMove move:allMoves) {
                if(!wouldBeInCheckAfterMove(board.getPiece(startPosition).getTeamColor(), move)){
                    validMoves.add(move);
                }
            }
            if(board.getPiece(startPosition).getPieceType() == ChessPiece.PieceType.KING){
                validMoves.addAll(getValidCastleMoves(startPosition));
            } else if(board.getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN){
                ChessMove enPassant = getValidEnPassantMove(startPosition);
                if(enPassant != null) {
                    validMoves.add(enPassant);
                }
            }
            return validMoves;
        } else {
            return new HashSet<>();
        }
    }

    private ChessMove getValidEnPassantMove(ChessPosition startPosition) {
        //check if piece is a pawn
        if(board.getPiece(startPosition) != null && board.getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN) {
            //check if the last move was a pawn directly to the right or left of startPosition
            if (lastMove != null && board.getPiece(lastMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.PAWN) {
                boolean sameRow = lastMove.getEndPosition().getRow() == startPosition.getRow();
                boolean columnsOffByOne = Math.abs(lastMove.getEndPosition().getColumn() - startPosition.getColumn()) == 1;
                if(sameRow && columnsOffByOne) {
                    //check if that was the enemy pawn's first move
                    if (board.getPiece(lastMove.getEndPosition()).getNumberOfMovesMade() == 1) {
                        if (board.getPiece(startPosition).getTeamColor() == TeamColor.WHITE) {
                            ChessPosition pos = new ChessPosition(startPosition.getRow() + 1, lastMove.getEndPosition().getColumn());
                            return new ChessMove(startPosition, pos, null);
                        } else {
                            ChessPosition pos = new ChessPosition(startPosition.getRow() - 1, lastMove.getEndPosition().getColumn());
                            return new ChessMove(startPosition, pos, null);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param startPosition position of the piece to check for valid castle moves
     * @return collection of valid castling moves
     */
    private Collection<ChessMove> getValidCastleMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = new HashSet<>();

        //check if piece is a king
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null || piece.getPieceType() != ChessPiece.PieceType.KING){
            return moves;
        }

        //check if the king has moved
        if(piece.getNumberOfMovesMade() != 0){
            return moves;
        }

        //check if in check
        if(isInCheck(piece.getTeamColor())) {
            return moves;
        }

        //check each square in direction dir for check or pieces, until you hit a rook
        for (int dir = -1; dir < 2; dir += 2) {
            ChessPosition pos = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + dir);
            boolean noCheck = true;
            while (pos.inBounds(8) && board.getPiece(pos) == null) {
                if (wouldBeInCheckAfterMove(piece.getTeamColor(), new ChessMove(startPosition, pos, null))) {
                    noCheck = false;
                }
                pos = new ChessPosition(startPosition.getRow(), pos.getColumn() + dir);
            }

            //check if the rook hasn't moved
            if (noCheck && pos.inBounds(8)){
                if(board.getPiece(pos).getPieceType() == ChessPiece.PieceType.ROOK && board.getPiece(pos).getNumberOfMovesMade() == 0) {
                    //if the rook hasn't moved, add a move to the moves collection
                    if (dir == 1) {
                        moves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 7), null));
                    } else {
                        moves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 3), null));
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece != null && piece.getTeamColor() == turnColor && validMoves(move.getStartPosition()).contains(move)){
            board.movePiece(move);
            turnColor = turnColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
            lastMove = move;
        } else {
            throw new InvalidMoveException();
        }
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
                    ChessPiece capturedPiece = boardToCheck.getPiece(move.getEndPosition());
                    if(capturedPiece != null && capturedPiece.getPieceType() == ChessPiece.PieceType.KING && capturedPiece.getTeamColor() == teamColor){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean pieceCanOnlyMoveIntoCheck(ChessPosition pos, TeamColor kingColor) {
        if(board.getPiece(pos).getTeamColor() == kingColor) {
            for (ChessMove move : validMoves(pos)) {
                if (!wouldBeInCheckAfterMove(kingColor, move)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     *
     * @param teamColor team to check for check
     * @param move move to perform before check
     * @return true if the team would be in check after the move
     */
    public boolean wouldBeInCheckAfterMove(TeamColor teamColor, ChessMove move){
        ChessBoard boardAfterMove = board.copy();
        try {
            boardAfterMove.movePiece(move);
        } catch (InvalidMoveException ex) {
            return true;
        }
        return(wouldBeInCheck(boardAfterMove, teamColor));
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            for(ChessPosition pos:getChessPositions(board)) {
                if(!pieceCanOnlyMoveIntoCheck(pos, teamColor)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            for (ChessPosition pos:getChessPositions(board)){
                if(board.getPiece(pos).getTeamColor() == teamColor){
                    if(!validMoves(pos).isEmpty()){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
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

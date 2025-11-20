package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardHandler {
    private ChessBoard board;
    private ChessGame.TeamColor color;

    public BoardHandler () {
        board = new ChessBoard();
        color = null;
    }

    public void updateBoard(ChessBoard board) {
        this.board = board;
        drawBoard();
    }

    public void drawBoard() {
        System.out.println(getBoardString());
    }

    public void setColor(ChessGame.TeamColor color) {
        this.color = color;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public String getBoardString() {
        StringBuilder result = new StringBuilder();
        boolean whiteTile;
        int[] rows, cols;
        if (color != ChessGame.TeamColor.BLACK){
            rows = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
            cols = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        } else {
            cols = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
            rows = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        }

        String labelRow = "   \u2003a \u2003b \u2003c \u2003d \u2003e \u2003f \u2003g \u2003h    ";
        if(color == ChessGame.TeamColor.BLACK){
            labelRow = new StringBuilder(labelRow).reverse().toString();
        }
        result.append(RESET_TEXT_BOLD_FAINT)
                .append(SET_TEXT_COLOR_LIGHT_GREY)
                .append(SET_BG_COLOR_DARK_GREY)
                .append(labelRow)
                .append(RESET_BG_COLOR)
                .append(RESET_TEXT_COLOR)
                .append("\n");
        for (int r: rows){
            result.append(SET_TEXT_COLOR_LIGHT_GREY)
                    .append(SET_BG_COLOR_DARK_GREY)
                    .append(" ")
                    .append(r+1)
                    .append(" ");
            for (int c: cols) {
                whiteTile = (r%2 + c%2)%2 == 1;
                result.append(whiteTile ? SET_BG_COLOR_WHITE : SET_BG_COLOR_LIGHT_GREY);
                ChessPiece piece = board.getPiece(new ChessPosition(r + 1, c + 1));
                if (piece == null){
                    result.append(EMPTY);
                } else {
                    if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                        result.append(SET_TEXT_COLOR_BLACK);
                    } else {
                        result.append(SET_TEXT_COLOR_DARK_GREY);
                    }
                    switch(piece.getPieceType()){
                        case ChessPiece.PieceType.PAWN ->
                                result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN: BLACK_PAWN);
                        case ChessPiece.PieceType.KING ->
                                result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING: BLACK_KING);
                        case ChessPiece.PieceType.QUEEN ->
                                result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN: BLACK_QUEEN);
                        case ChessPiece.PieceType.BISHOP ->
                                result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP: BLACK_BISHOP);
                        case ChessPiece.PieceType.KNIGHT ->
                                result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT: BLACK_KNIGHT);
                        case ChessPiece.PieceType.ROOK ->
                                result.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK: BLACK_ROOK);
                    }
                }
            }
            result.append(SET_TEXT_COLOR_LIGHT_GREY).
                    append(SET_BG_COLOR_DARK_GREY)
                    .append(" ")
                    .append(r+1)
                    .append(" ")
                    .append(RESET_BG_COLOR)
                    .append(RESET_TEXT_COLOR)
                    .append("\n");
        }
        result.append(SET_TEXT_COLOR_LIGHT_GREY)
                .append(SET_BG_COLOR_DARK_GREY)
                .append(labelRow)
                .append(RESET_BG_COLOR)
                .append(RESET_TEXT_COLOR)
                .append("\n");
        return result.toString();
    }
}

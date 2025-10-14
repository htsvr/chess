package dataobjects;

import chess.ChessGame;

public record JoinRequest(ChessGame.TeamColor playerColor, int gameID, String authToken) {
}

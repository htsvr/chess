package dataobjects;

import chess.ChessGame;

public record JoinRequest(ChessGame.TeamColor color, int gameID, AuthData authToken) {
}

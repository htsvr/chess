package websocket.messages;

import chess.ChessGame;

public class LoadGameServerMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameServerMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}

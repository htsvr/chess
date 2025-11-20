package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class LoadGameServerMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameServerMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        LoadGameServerMessage that = (LoadGameServerMessage) o;
        return Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(game);
        return result;
    }
}

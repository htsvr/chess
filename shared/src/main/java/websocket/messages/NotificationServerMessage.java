package websocket.messages;

import java.util.Objects;

public class NotificationServerMessage extends ServerMessage {
    private final String message;

    public NotificationServerMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        NotificationServerMessage that = (NotificationServerMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(message);
        return result;
    }
}

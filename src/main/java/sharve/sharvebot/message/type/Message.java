package sharve.sharvebot.message.type;
public record Message(
    MessageType type,
    MessageData data
) {
    @Override
    public final String toString() {
        if (type == MessageType.TEXT) {
            return data.text();
        } else if (type == MessageType.IMAGE) {
            return data.summary();
        } else {
            throw new IllegalArgumentException("Unknown MessageType: " + type);
        }
    }
}

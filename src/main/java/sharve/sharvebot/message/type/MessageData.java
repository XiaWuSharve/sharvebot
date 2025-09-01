package sharve.sharvebot.message.type;
public record MessageData(
    String text,
    String summary
) {
    @Override
    public final String toString() {
        return "MessageData [text=" + text + ", summary=" + summary + "]";
    }
}

package sharve.sharvebot.message.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {

    PRIVATE, POKE, TEXT, IMAGE, UNKNOWN;

    @JsonCreator
    public static MessageType fromString(String value) {
        try {
            return MessageType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}

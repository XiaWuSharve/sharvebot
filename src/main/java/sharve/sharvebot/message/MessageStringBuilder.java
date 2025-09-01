package sharve.sharvebot.message;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sharve.sharvebot.storage.MessageEntity;

public class MessageStringBuilder {

    private List<MessageEntity> messages = new ArrayList<>();
    private final DateTimeFormatter dateTimeFormat;

    public MessageStringBuilder(DateTimeFormatter dateFormat) {
        this.dateTimeFormat = dateFormat;
    }

    public void clear() {
        this.messages.clear();
    }

    public void add(MessageEntity message) {
        this.messages.add(message);
    }

    @Override
    public String toString() {
        return messages.stream()
                .map(m -> dateTimeFormat.format(m.getDate().toInstant().atZone(ZoneId.systemDefault())) + "|" + m.getMessage())
                .collect(Collectors.joining("\n"));
    }
}

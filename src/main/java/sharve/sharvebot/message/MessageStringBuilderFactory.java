package sharve.sharvebot.message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import sharve.sharvebot.message.type.AllMessage;
import sharve.sharvebot.message.type.MessageType;

@Component
public class MessageStringBuilderFactory {

    private final DateTimeFormatter dateTimeFormat;
    private final static String REGEX = "(\\d{2}:\\d{2}:\\d{2})\\|(.*?)(?=\\d{2}:\\d{2}:\\d{2}\\||$)";

    public MessageStringBuilderFactory(DateTimeFormatter dateFormat) {
        this.dateTimeFormat = dateFormat;
    }

    public MessageStringBuilder create() {
        return new MessageStringBuilder(dateTimeFormat);
    }

    public List<AllMessage> parse(String messages) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(messages);

        List<AllMessage> allMessage = matcher.results().map(item -> {
            LocalTime time = LocalTime.from(dateTimeFormat.parse(item.group(1)));
            ZonedDateTime targetDateTime = LocalDateTime.of(LocalDate.now(), time).atZone(ZoneId.systemDefault());
            if (targetDateTime.isBefore(ZonedDateTime.now())) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Date date = Date.from(targetDateTime.toInstant());
            String text = item.group(2);
            MessageType messageType;
            if (text == null)
                throw new RuntimeException("Failed to parse message on offset 1, raw message: " + messages);
            text = text.trim();
            if (text.equals("[拍一拍消息]")) {
                messageType = MessageType.POKE;
            } else {
                messageType = MessageType.PRIVATE;
            }
            //添加ai生成标识符"*"
            return new AllMessage(messageType, date, text + "*");
        }).toList();
        return allMessage;
    }
}

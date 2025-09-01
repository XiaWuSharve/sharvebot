package sharve.sharvebot.message.type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AllMessage {
    Long messageId;
    Date date;
    Long userId;
    Long targetId;
    MessageType type;
    List<Message> message;

    public AllMessage() {}
    public AllMessage(MessageType type, Date date, String text) {
        this.type = type;
        this.date = date;
        this.message = new ArrayList<>();
        this.message.add(new Message(MessageType.TEXT, new MessageData(text, text)));
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Date getDate() {
        return date;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public List<Message> getMessage() {
        return message;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "AllMessage [date=" + date + ", message=" + message + ", targetId=" + targetId + ", type=" + type
                + ", userId=" + userId + "]";
    }
}
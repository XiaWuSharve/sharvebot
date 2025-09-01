package sharve.sharvebot.message.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import sharve.sharvebot.message.type.Message;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PrivateMessageDTO implements Serializable {
    Long userId;
    List<Message> message;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }
}

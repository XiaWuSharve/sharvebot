package sharve.sharvebot.storage;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("message")
public class MessageEntity {

    @Id
    String id;
    Date date;
    Long userId;
    @Indexed
    Long targetId;
    String message;

    public MessageEntity() {
        this.userId = 0L;
    }

    public MessageEntity(Date date, String message) {
        this();
        this.date = date;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
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

    public void setDate(Long date) {
        this.date = new Date(date);
    }

    @Override
    public String toString() {
        return "AllMessage [date=" + date + ", message=" + message + ", targetId=" + targetId
                + ", userId=" + userId + "]";
    }
}
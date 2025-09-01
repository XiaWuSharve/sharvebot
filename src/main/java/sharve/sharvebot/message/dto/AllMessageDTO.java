package sharve.sharvebot.message.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import sharve.sharvebot.message.type.Message;
import sharve.sharvebot.message.type.MessageType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AllMessageDTO {
    Long messageId;
    Long time;
    Long userId;
    Long targetId;
    MessageType subType;
    MessageType messageType;
    List<Message> message;
    Long groupId;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
    
    public MessageType messageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
    
    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public MessageType subType() {
        return subType;
    }

    public void setSubType(MessageType subType) {
        this.subType = subType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "AllMessageDTO{" +
                "messageId=" + messageId +
                "targetId=" + targetId +
                ", userId=" + userId +
                ", time=" + time +
                ", subType=" + subType +
                ", messageType=" + messageType +
                ", message=" + message +
                '}';
    }
}
package sharve.sharvebot.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.openai.models.chat.completions.ChatCompletionAssistantMessageParam;
import com.openai.models.chat.completions.ChatCompletionMessageParam;
import com.openai.models.chat.completions.ChatCompletionUserMessageParam;

import sharve.sharvebot.message.MessageStringBuilder;
import sharve.sharvebot.message.MessageStringBuilderFactory;

@Service
public class StorageService {
    private final MessageRepository messageRepository;
    private MessageStringBuilderFactory messageStringBuilderFactory;

    public StorageService(MessageRepository messageRepository,
            MessageStringBuilderFactory messageStringBuilderFactory) {
        this.messageRepository = messageRepository;
        this.messageStringBuilderFactory = messageStringBuilderFactory;
    }

    public List<ChatCompletionMessageParam> getRecentContextToChatCompletionMessageParam(Long userId) {
        List<MessageEntity> messageEntities = messageRepository.findTop1000ByTargetIdOrderByDateDesc(userId);
        Collections.reverse(messageEntities);
        List<ChatCompletionMessageParam> mergedMessages = new ArrayList<>();
        MessageStringBuilder currentMessage = messageStringBuilderFactory.create();
        boolean isSelf = false;
        boolean isFirst = true;
        messageEntities.add(new MessageEntity());
        for (MessageEntity messageEntity : messageEntities) {
            // getUserId为发送者Id，userId为小窗对方Id
            boolean self = !messageEntity.getUserId().equals(userId);
            if (isFirst) {
                if (self)
                    continue;
                else
                    isFirst = false;
            }

            if (isSelf != self) {
                ChatCompletionMessageParam messageParam;
                if (isSelf) {
                    messageParam = ChatCompletionMessageParam.ofAssistant(
                            ChatCompletionAssistantMessageParam.builder()
                                    .content(currentMessage.toString()).build());
                } else {
                    messageParam = ChatCompletionMessageParam.ofUser(
                            ChatCompletionUserMessageParam.builder()
                                    .content(currentMessage.toString()).build());
                }
                mergedMessages.add(messageParam);
                currentMessage.clear();
                isSelf = self;
            }
            currentMessage.add(messageEntity);
        }
        return mergedMessages;
    }
}

package sharve.sharvebot.scheduler;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import sharve.sharvebot.communication.NapCatRestTemplate;
import sharve.sharvebot.message.MessageHelper;
import sharve.sharvebot.message.dto.MessageResponseDTO;
import sharve.sharvebot.message.type.AllMessage;
import sharve.sharvebot.message.type.MessageType;

@Component
public class SendMessageJob implements Job {

    private final List<Long> messageIdList;

    private final MessageHelper messageHelper;
    private final NapCatRestTemplate template;
    private final static Logger logger = LoggerFactory.getLogger(SendMessageJob.class);

    public SendMessageJob(NapCatRestTemplate template, MessageHelper messageHelper, List<Long> messageIdList) {
        this.template = template;
        this.messageHelper = messageHelper;
        this.messageIdList = messageIdList;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        AllMessage message = (AllMessage) dataMap.get("message");

        logger.info("SendMess job started, message: {}", message.getMessage().toString());

        MessageResponseDTO messageResponseDTO;
        if (message.getType() == MessageType.PRIVATE) {
            messageResponseDTO = template.postForObject("/send_private_msg",
                    messageHelper.allMessageToPrivateMessageDTO(message),
                    MessageResponseDTO.class);
        } else if (message.getType() == MessageType.POKE) {
            messageResponseDTO = template.postForObject("/group_poke",
                    messageHelper.allMessageToPokeMessageDTO(message),
                    MessageResponseDTO.class);
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + message.getType());
        }

        if (messageResponseDTO == null) {
            throw new InternalError("messageResponseDTO is null");
        }

        if (messageResponseDTO.getData() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(messageResponseDTO.getRetcode()),
                    messageResponseDTO.getWording());
        }

        if (messageResponseDTO.getData().getMessageId() != null) {
            Long messageId = messageResponseDTO.getData().getMessageId();
            messageIdList.add(messageId);
            logger.info("Cached messageId: {}", messageId);
        }
    }
}

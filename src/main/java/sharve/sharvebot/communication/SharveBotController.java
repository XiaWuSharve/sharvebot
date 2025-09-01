package sharve.sharvebot.communication;

import org.springframework.web.bind.annotation.RestController;

import sharve.sharvebot.message.MessageHelper;
import sharve.sharvebot.message.dto.AllMessageDTO;
import sharve.sharvebot.message.type.AllMessage;
import sharve.sharvebot.message.type.MessageType;
import sharve.sharvebot.scheduler.GenerateTextJob;
import sharve.sharvebot.storage.MessageEntity;
import sharve.sharvebot.storage.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class SharveBotController {
    private final static Logger logger = LoggerFactory.getLogger(SharveBotController.class);
    private final MessageRepository messageRepository;
    private final MessageHelper messageHelper;
    private final List<Long> messageIdList;
    private final Scheduler scheduler;

    public SharveBotController(MessageHelper messageHelper, MessageRepository messageRepository,
            List<Long> messageIdList, Scheduler scheduler) {
        this.messageHelper = messageHelper;
        this.messageRepository = messageRepository;
        this.messageIdList = messageIdList;
        this.scheduler = scheduler;
    }

    @GetMapping("/")
    public String helloWorld() {
        return "Hello Sharve Bot!";
    }

    @PostMapping("/")
    public String handleMessage(@RequestBody AllMessageDTO allMessageDto) {
        if (MessageHelper.convertMessageType(allMessageDto) == MessageType.UNKNOWN) {
            return "Unknown message type";
        }

        AllMessage allMessage = messageHelper.allMessageDTOToAllMessage(allMessageDto);
        MessageEntity messageEntity = messageHelper.allMessageToMessageEntity(allMessage);
        messageRepository.save(messageEntity);
        logger.info("Received request, message: {}", messageEntity.getMessage());
        logger.info("allMessageDto: {}", allMessageDto.toString());

        if (allMessage.getMessageId() != null &&
                messageIdList.contains(allMessage.getMessageId())) {
            messageIdList.remove(allMessage.getMessageId());
            logger.info("Cached message, skip, message: {}", messageEntity.getMessage());
        } else {
            GroupMatcher<JobKey> groupMatcher = GroupMatcher.jobGroupEquals(allMessage.getTargetId().toString());
            List<JobKey> sendMessJobs;
            try {
                JobKey genTextJob = JobKey.jobKey(allMessage.getTargetId().toString());
                scheduler.deleteJob(genTextJob);
                sendMessJobs = new ArrayList<>(scheduler.getJobKeys(groupMatcher));
                scheduler.deleteJobs(sendMessJobs);
                logger.info("Deleted jobs: {} {}", genTextJob.getGroup() + ":" + genTextJob.getName(),
                        sendMessJobs.stream().map(j -> j.getGroup() + ":" + j.getName())
                                .collect(Collectors.joining(" ")));
            } catch (SchedulerException e) {
                logger.error("deleteJobs error", e);
            }
        }

        if (allMessage.getUserId().equals(allMessage.getTargetId())) {
            JobDetail jobDetail = JobBuilder.newJob(GenerateTextJob.class)
                    .withIdentity(allMessage.getTargetId().toString())
                    .usingJobData("userId", allMessage.getTargetId())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(allMessage.getTargetId().toString())
                    .startNow()
                    .build();

            try {
                scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Successfully schedule genText, message: {}, will start at: {}", messageEntity.getMessage(),
                        trigger.getStartTime());
            } catch (SchedulerException e) {
                logger.error(allMessage.toString(), e);
            }
            return "Successfully created a task";
        }

        logger.info("Successfully exited, targetId: {}", allMessageDto.getTargetId());

        return "Successfully kept clean";
    }

    // @PostMapping("/")
    // public String getRawMessage(@RequestBody Object allMessageDto) {
    // logger.info(allMessageDto.toString());
    // return allMessageDto.toString();
    // }
}

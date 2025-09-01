package sharve.sharvebot.scheduler;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.openai.models.chat.completions.ChatCompletionMessageParam;

import sharve.sharvebot.communication.LlmRestTemplate;
import sharve.sharvebot.message.MessageStringBuilderFactory;
import sharve.sharvebot.message.type.AllMessage;
import sharve.sharvebot.storage.StorageService;

@Component
public class GenerateTextJob implements Job {

    private final MessageStringBuilderFactory messageStringBuilderFactory;
    private final LlmRestTemplate llmRestTemplate;
    private final StorageService storageService;
    private final Scheduler scheduler;
    private final static Logger logger = LoggerFactory.getLogger(GenerateTextJob.class);

    public GenerateTextJob(LlmRestTemplate llmRestTemplate, StorageService storageService,
            MessageStringBuilderFactory messageStringBuilderFactory,
            Scheduler scheduler) {
        this.llmRestTemplate = llmRestTemplate;
        this.storageService = storageService;
        this.messageStringBuilderFactory = messageStringBuilderFactory;
        this.scheduler = scheduler;
    }

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        Long userId = dataMap.getLong("userId");

        logger.info("GenText job started, userId: {}", userId);
        List<ChatCompletionMessageParam> params = storageService.getRecentContextToChatCompletionMessageParam(userId);
        String result = llmRestTemplate.create(params);
        List<AllMessage> resultMessages = messageStringBuilderFactory.parse(result);
        int i = 0;
        for (AllMessage m : resultMessages) {
            m.setTargetId(userId);
            JobDetail jobDetail = JobBuilder.newJob(SendMessageJob.class)
                    .withIdentity(String.valueOf(i++), userId.toString())
                    .build();
            jobDetail.getJobDataMap().put("message", m);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(i++), userId.toString())
                    .startAt(m.getDate())
                    .build();

            try {
                scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Successfully schedule sendMess, message: {}, will start at: {}", m.getMessage().toString(),
                        trigger.getStartTime());
            } catch (SchedulerException e) {
                logger.error(resultMessages.toString(), e);
            }
        }
    }
}

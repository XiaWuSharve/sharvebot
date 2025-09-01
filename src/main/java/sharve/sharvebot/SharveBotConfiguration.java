package sharve.sharvebot;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import sharve.sharvebot.message.MessageHelper;
import sharve.sharvebot.message.dto.AllMessageDTO;
import sharve.sharvebot.message.dto.PokeMessageDTO;
import sharve.sharvebot.message.dto.PrivateMessageDTO;
import sharve.sharvebot.message.type.AllMessage;
import sharve.sharvebot.message.type.Message;
import sharve.sharvebot.message.type.MessageType;
import sharve.sharvebot.storage.MessageEntity;

@Configuration
public class SharveBotConfiguration {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        TypeMap<AllMessageDTO, AllMessage> allMessageTypeMap = modelMapper.createTypeMap(AllMessageDTO.class,
                AllMessage.class);
        Converter<AllMessageDTO, MessageType> messageTypeConverter = ctx -> {
            AllMessageDTO source = ctx.getSource();
            return MessageHelper.convertMessageType(source);
        };
        Converter<AllMessageDTO, Long> targetIdConverter = ctx -> {
            AllMessageDTO source = ctx.getSource();
            if (MessageHelper.convertMessageType(source) == MessageType.POKE)
                return source.getUserId();
            else
                return source.getTargetId();
        };
        Converter<Long, Date> dateConverter = ctx -> {
            Long time = ctx.getSource();
            return new Date(time * 1000L);
        };

        allMessageTypeMap.addMappings(mapper -> {
            mapper.using(messageTypeConverter).map(src -> src, AllMessage::setType);
            mapper.using(dateConverter).map(AllMessageDTO::getTime, AllMessage::setDate);
            mapper.using(targetIdConverter).map(src -> src, AllMessage::setTargetId);
        });

        TypeMap<AllMessage, PrivateMessageDTO> privateMessageDtoTypeMap = modelMapper.createTypeMap(AllMessage.class,
                PrivateMessageDTO.class);
        
        privateMessageDtoTypeMap.addMapping(AllMessage::getTargetId, PrivateMessageDTO::setUserId);

        TypeMap<AllMessage, PokeMessageDTO> pokeMessageDtoTypeMap = modelMapper.createTypeMap(AllMessage.class,
                PokeMessageDTO.class);
        pokeMessageDtoTypeMap.addMapping(AllMessage::getTargetId, PokeMessageDTO::setUserId);

        TypeMap<AllMessage, MessageEntity> messageEntityTypeMap = modelMapper.createTypeMap(AllMessage.class,
                MessageEntity.class);
        Converter<AllMessage, String> messageEntityConverter = ctx -> {
            AllMessage source = ctx.getSource();

            if (source.getType() == MessageType.PRIVATE) {
                return String.join("", source.getMessage().stream().map(Message::toString).toList());
            } else if (source.getType() == MessageType.POKE) {
                return "[拍一拍消息]";
            } else {
                throw new IllegalArgumentException("MessageType is not PRIVATE or POKE, get " + source.getType());
            }
        };
        messageEntityTypeMap
                .addMappings(mapper -> mapper.using(messageEntityConverter).map(src -> src, MessageEntity::setMessage));

        return modelMapper;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    List<Long> messageIdList() {
        return new ArrayList<Long>();
    }

    @Bean
    DateTimeFormatter dateFormat() {
        return DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    // @Bean
    // JobDetailFactoryBean jobDetailFactoryBean() {
    // JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
    // jobDetailFactoryBean.setDurability(true);
    // return jobDetailFactoryBean;
    // }
}

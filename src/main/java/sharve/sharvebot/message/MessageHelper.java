package sharve.sharvebot.message;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import sharve.sharvebot.message.dto.AllMessageDTO;
import sharve.sharvebot.message.dto.PokeMessageDTO;
import sharve.sharvebot.message.dto.PrivateMessageDTO;
import sharve.sharvebot.message.type.AllMessage;
import sharve.sharvebot.message.type.MessageType;
import sharve.sharvebot.storage.MessageEntity;

@Component
public class MessageHelper {
    private final ModelMapper modelMapper;

    public MessageHelper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public static MessageType convertMessageType(AllMessageDTO allMessageDTO) {
        if (allMessageDTO.messageType() == MessageType.PRIVATE) {
            return MessageType.PRIVATE;
        } else if (allMessageDTO.subType() == MessageType.POKE &&
                allMessageDTO.getGroupId() == null) {
            return MessageType.POKE;
        } else {
            return MessageType.UNKNOWN;
        }
    }

    public AllMessage allMessageDTOToAllMessage(AllMessageDTO allMessageDto) {
        // 其实可以改成bean
        // TypeMap<AllMessageDTO, AllMessage> typeMap = modelMapper.typeMap(AllMessageDTO.class, AllMessage.class);
        // Converter<AllMessageDTO, MessageType> converter = (ctx -> MessageHelper.convertMessageType(ctx.getSource()));
        // typeMap.addMappings(mapper -> {
        //     mapper.map(src -> new Date(src.getTime() * 1000L), AllMessage::setDate);
        //     mapper.using(converter).map(src -> src, AllMessage::setType);
        // });

        AllMessage allMessage = modelMapper.map(allMessageDto, AllMessage.class);
        return allMessage;
    }

    public PrivateMessageDTO allMessageToPrivateMessageDTO(AllMessage allMessage) {
        // if (allMessage.getType() != MessageType.PRIVATE) {
        //     throw new IllegalArgumentException("MessageType is not PRIVATE, get " + allMessage.getType());
        // }
        // TypeMap<AllMessage, PrivateMessageDTO> typeMap = modelMapper.typeMap(AllMessage.class,
        //         PrivateMessageDTO.class);
        // typeMap.addMapping(AllMessage::getTargetId, PrivateMessageDTO::setUserId);
        PrivateMessageDTO privateMessage = modelMapper.map(allMessage, PrivateMessageDTO.class);
        return privateMessage;
    }

    public PokeMessageDTO allMessageToPokeMessageDTO(AllMessage allMessage) {
        // if (allMessage.getType() != MessageType.POKE) {
        //     throw new IllegalArgumentException("MessageType is not POKE, get " + allMessage.getType());
        // }
        // TypeMap<AllMessage, PokeMessageDTO> typeMap = modelMapper.typeMap(AllMessage.class,
        //         PokeMessageDTO.class);
        // typeMap.addMapping(AllMessage::getTargetId, PokeMessageDTO::setUserId);
        PokeMessageDTO pokeMessage = modelMapper.map(allMessage, PokeMessageDTO.class);
        return pokeMessage;
    }

    public MessageEntity allMessageToMessageEntity(AllMessage allMessage) {
        // TypeMap<AllMessage, MessageEntity> typeMap = modelMapper.typeMap(AllMessage.class, MessageEntity.class);
        // typeMap.addMapping(src -> {
        //     if (src.getType() == MessageType.PRIVATE) {
        //         return String.join("", src.getMessage().stream().map(Message::toString).toList());
        //     } else if (src.getType() == MessageType.POKE) {
        //         return "[拍一拍消息]";
        //     } else {
        //         throw new IllegalArgumentException("MessageType is not PRIVATE or POKE, get " + allMessage.getType());
        //     }
        // }, MessageEntity::setMessage);
        MessageEntity messageEntity = modelMapper.map(allMessage, MessageEntity.class);
        return messageEntity;
    }
}

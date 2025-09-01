package sharve.sharvebot.storage;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<MessageEntity, String> {
    List<MessageEntity> findTop1000ByTargetIdOrderByDateDesc(Long targetId);
}

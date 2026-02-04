package tech.maze.helloworld.backend.infrastructure.persistence.repositories;

import java.util.UUID;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import tech.maze.helloworld.backend.infrastructure.persistence.entities.MessageEntity;

/**
 * JPA repository interface for managing {@link MessageEntity} entities.
 */
@Repository
public interface MessageJpaRepository extends ListCrudRepository<MessageEntity, UUID> {

}


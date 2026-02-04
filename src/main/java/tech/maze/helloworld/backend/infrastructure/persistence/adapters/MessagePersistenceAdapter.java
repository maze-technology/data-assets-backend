package tech.maze.helloworld.backend.infrastructure.persistence.adapters;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.out.LoadMessagePort;
import tech.maze.helloworld.backend.domain.ports.out.SaveMessagePort;
import tech.maze.helloworld.backend.infrastructure.persistence.entities.MessageEntity;
import tech.maze.helloworld.backend.infrastructure.persistence.mappers.MessageEntityMapper;
import tech.maze.helloworld.backend.infrastructure.persistence.repositories.MessageJpaRepository;

/**
 * Adapter that implements the output ports for message persistence.
 * This adapter bridges the domain layer with the JPA persistence layer.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessagePersistenceAdapter implements LoadMessagePort, SaveMessagePort {
  MessageJpaRepository messageJpaRepository;
  MessageEntityMapper messageEntityMapper;

  @Override
  public List<Message> loadAllMessages() {
    return messageJpaRepository.findAll()
        .stream()
        .map(messageEntityMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Message saveMessage(Message message) {
    final MessageEntity entity = messageEntityMapper.toEntity(message);
    final MessageEntity savedEntity = messageJpaRepository.save(entity);

    return messageEntityMapper.toDomain(savedEntity);
  }
}


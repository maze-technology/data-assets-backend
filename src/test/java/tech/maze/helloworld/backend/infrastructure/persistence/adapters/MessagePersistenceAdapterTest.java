package tech.maze.helloworld.backend.infrastructure.persistence.adapters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.infrastructure.persistence.entities.MessageEntity;
import tech.maze.helloworld.backend.infrastructure.persistence.mappers.MessageEntityMapper;
import tech.maze.helloworld.backend.infrastructure.persistence.repositories.MessageJpaRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagePersistenceAdapterTest {
  @Mock
  private MessageJpaRepository messageJpaRepository;

  @Mock
  private MessageEntityMapper messageEntityMapper;

  private MessagePersistenceAdapter messagePersistenceAdapter;

  @BeforeEach
  void setUp() {
    messagePersistenceAdapter = new MessagePersistenceAdapter(
        messageJpaRepository,
        messageEntityMapper
    );
  }

  @Test
  @DisplayName("Should load all messages and convert entities to domain models")
  void loadAllMessages_ShouldLoadAndConvertAllMessages() {
    // Arrange
    final UUID id1 = UUID.randomUUID();
    final UUID id2 = UUID.randomUUID();
    final Instant createdAt = Instant.now();

    final MessageEntity entity1 = MessageEntity.builder()
        .id(id1)
        .content("Message 1")
        .createdAt(createdAt)
        .build();
    final MessageEntity entity2 = MessageEntity.builder()
        .id(id2)
        .content("Message 2")
        .createdAt(createdAt)
        .build();

    final Message domain1 = Message.builder()
        .id(id1)
        .content("Message 1")
        .createdAt(createdAt)
        .build();
    final Message domain2 = Message.builder()
        .id(id2)
        .content("Message 2")
        .createdAt(createdAt)
        .build();

    when(messageJpaRepository.findAll()).thenReturn(List.of(entity1, entity2));
    when(messageEntityMapper.toDomain(entity1)).thenReturn(domain1);
    when(messageEntityMapper.toDomain(entity2)).thenReturn(domain2);

    // Act
    final List<Message> result = messagePersistenceAdapter.loadAllMessages();

    // Assert
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(domain1, domain2);
    verify(messageJpaRepository).findAll();
    verify(messageEntityMapper).toDomain(entity1);
    verify(messageEntityMapper).toDomain(entity2);
  }

  @Test
  @DisplayName("Should return empty list when no messages exist")
  void loadAllMessages_ShouldReturnEmptyListWhenNoMessages() {
    // Arrange
    when(messageJpaRepository.findAll()).thenReturn(Collections.emptyList());

    // Act
    final List<Message> result = messagePersistenceAdapter.loadAllMessages();

    // Assert
    assertThat(result).isEmpty();
    verify(messageJpaRepository).findAll();
    verify(messageEntityMapper, never()).toDomain(any());
  }

  @Test
  @DisplayName("Should save message by converting to entity, saving, and converting back to domain")
  void saveMessage_ShouldSaveAndConvertCorrectly() {
    // Arrange
    final UUID id = UUID.randomUUID();
    final Instant createdAt = Instant.now();

    final Message domainMessage = Message.builder()
        .content("Test message")
        .build();

    final MessageEntity entity = MessageEntity.builder()
        .content("Test message")
        .build();

    final MessageEntity savedEntity = MessageEntity.builder()
        .id(id)
        .content("Test message")
        .createdAt(createdAt)
        .build();

    final Message savedDomainMessage = Message.builder()
        .id(id)
        .content("Test message")
        .createdAt(createdAt)
        .build();

    when(messageEntityMapper.toEntity(domainMessage)).thenReturn(entity);
    when(messageJpaRepository.save(entity)).thenReturn(savedEntity);
    when(messageEntityMapper.toDomain(savedEntity)).thenReturn(savedDomainMessage);

    // Act
    final Message result = messagePersistenceAdapter.saveMessage(domainMessage);

    // Assert
    assertThat(result).isEqualTo(savedDomainMessage);
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getContent()).isEqualTo("Test message");
    verify(messageEntityMapper).toEntity(domainMessage);
    verify(messageJpaRepository).save(entity);
    verify(messageEntityMapper).toDomain(savedEntity);
  }

  @Test
  @DisplayName("Should save message with existing ID and preserve it")
  void saveMessage_ShouldPreserveExistingId() {
    // Arrange
    final UUID existingId = UUID.randomUUID();
    final Instant createdAt = Instant.now();

    final Message domainMessage = Message.builder()
        .id(existingId)
        .content("Updated message")
        .createdAt(createdAt)
        .build();

    final MessageEntity entity = MessageEntity.builder()
        .id(existingId)
        .content("Updated message")
        .createdAt(createdAt)
        .build();

    final MessageEntity savedEntity = MessageEntity.builder()
        .id(existingId)
        .content("Updated message")
        .createdAt(createdAt)
        .build();

    final Message savedDomainMessage = Message.builder()
        .id(existingId)
        .content("Updated message")
        .createdAt(createdAt)
        .build();

    when(messageEntityMapper.toEntity(domainMessage)).thenReturn(entity);
    when(messageJpaRepository.save(entity)).thenReturn(savedEntity);
    when(messageEntityMapper.toDomain(savedEntity)).thenReturn(savedDomainMessage);

    // Act
    final Message result = messagePersistenceAdapter.saveMessage(domainMessage);

    // Assert
    assertThat(result).isEqualTo(savedDomainMessage);
    assertThat(result.getId()).isEqualTo(existingId);
    verify(messageEntityMapper).toEntity(domainMessage);
    verify(messageJpaRepository).save(entity);
    verify(messageEntityMapper).toDomain(savedEntity);
  }
}


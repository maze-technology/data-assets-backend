package tech.maze.helloworld.backend.domain.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.out.LoadMessagePort;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllMessagesServiceTest {
  @Mock
  private LoadMessagePort loadMessagePort;

  private GetAllMessagesService getAllMessagesService;

  @BeforeEach
  void setUp() {
    getAllMessagesService = new GetAllMessagesService(loadMessagePort);
  }

  @Test
  @DisplayName("Should return all messages from port")
  void getAllMessages_ShouldReturnAllMessages() {
    // Arrange
    final Message message1 = Message.builder()
        .id(UUID.randomUUID())
        .content("Message 1")
        .build();
    final Message message2 = Message.builder()
        .id(UUID.randomUUID())
        .content("Message 2")
        .build();
    final List<Message> expectedMessages = List.of(message1, message2);

    when(loadMessagePort.loadAllMessages()).thenReturn(expectedMessages);

    // Act
    final List<Message> actualMessages = getAllMessagesService.getAllMessages();

    // Assert
    assertThat(actualMessages).isEqualTo(expectedMessages);
    verify(loadMessagePort).loadAllMessages();
  }

  @Test
  @DisplayName("Should return empty list when port returns empty list")
  void getAllMessages_ShouldReturnEmptyListWhenNoMessages() {
    // Arrange
    when(loadMessagePort.loadAllMessages()).thenReturn(Collections.emptyList());

    // Act
    final List<Message> actualMessages = getAllMessagesService.getAllMessages();

    // Assert
    assertThat(actualMessages).isEmpty();
    verify(loadMessagePort).loadAllMessages();
  }
}


package tech.maze.helloworld.backend.domain.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.out.SaveMessagePort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveMessageServiceTest {
  @Mock
  private SaveMessagePort saveMessagePort;

  private SaveMessageService saveMessageService;

  @BeforeEach
  void setUp() {
    saveMessageService = new SaveMessageService(saveMessagePort);
  }

  @Test
  @DisplayName("Should save message and return saved message")
  void saveMessage_ShouldSaveAndReturnMessage() {
    // Arrange
    final Message message = Message.builder()
        .content("Test message")
        .build();
    final Message savedMessage = Message.builder()
        .id(UUID.randomUUID())
        .content("Test message")
        .build();

    when(saveMessagePort.saveMessage(message)).thenReturn(savedMessage);

    // Act
    final Message result = saveMessageService.saveMessage(message);

    // Assert
    assertThat(result).isEqualTo(savedMessage);
    verify(saveMessagePort).saveMessage(message);
  }
}


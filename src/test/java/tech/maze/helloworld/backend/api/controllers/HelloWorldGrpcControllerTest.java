package tech.maze.helloworld.backend.api.controllers;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.dtos.helloworld.payloads.GetAllRequest;
import tech.maze.dtos.helloworld.payloads.GetAllResponse;
import tech.maze.helloworld.backend.api.mappers.MessageDtoMapper;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.in.GetAllMessagesUseCase;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelloWorldGrpcControllerTest {
  @Mock
  private GetAllMessagesUseCase getAllMessagesUseCase;

  @Mock
  private MessageDtoMapper messageDtoMapper;

  @Mock
  private StreamObserver<GetAllResponse> getAllResponseObserver;

  private HelloWorldGrpcController controller;

  @BeforeEach
  void setUp() {
    controller = new HelloWorldGrpcController(getAllMessagesUseCase, messageDtoMapper);
  }

  @Test
  @DisplayName("Should return all messages when getting all messages")
  void getAll_ShouldReturnAllMessages() {
    // Arrange
    final GetAllRequest request = GetAllRequest.newBuilder().build();
    final Message message1 = Message.builder()
        .id(UUID.randomUUID())
        .content("Message 1")
        .build();
    final Message message2 = Message.builder()
        .id(UUID.randomUUID())
        .content("Message 2")
        .build();

    final tech.maze.dtos.helloworld.models.Message messageDto1 = tech.maze.dtos.helloworld.models.Message.newBuilder()
        .setId(message1.getId().toString())
        .setContent(message1.getContent())
        .build();
    final tech.maze.dtos.helloworld.models.Message messageDto2 = tech.maze.dtos.helloworld.models.Message.newBuilder()
        .setId(message2.getId().toString())
        .setContent(message2.getContent())
        .build();

    final GetAllResponse expectedResponse = GetAllResponse.newBuilder()
        .addAllMessages(List.of(messageDto1, messageDto2))
        .build();

    when(getAllMessagesUseCase.getAllMessages()).thenReturn(List.of(message1, message2));
    when(messageDtoMapper.toDto(message1)).thenReturn(messageDto1);
    when(messageDtoMapper.toDto(message2)).thenReturn(messageDto2);

    // Act
    controller.getAll(request, getAllResponseObserver);

    // Assert
    verify(getAllMessagesUseCase).getAllMessages();
    verify(messageDtoMapper).toDto(message1);
    verify(messageDtoMapper).toDto(message2);
    verify(getAllResponseObserver).onNext(expectedResponse);
    verify(getAllResponseObserver).onCompleted();
    verify(getAllResponseObserver, never()).onError(any());
  }

  @Test
  @DisplayName("Should return empty response when no messages exist")
  void getAll_ShouldReturnEmptyResponseWhenNoMessages() {
    // Arrange
    final GetAllRequest request = GetAllRequest.newBuilder().build();
    final GetAllResponse expectedResponse = GetAllResponse.newBuilder().build();

    when(getAllMessagesUseCase.getAllMessages()).thenReturn(Collections.emptyList());

    // Act
    controller.getAll(request, getAllResponseObserver);

    // Assert
    verify(getAllMessagesUseCase).getAllMessages();
    verify(messageDtoMapper, never()).toDto(any());
    verify(getAllResponseObserver).onNext(expectedResponse);
    verify(getAllResponseObserver).onCompleted();
    verify(getAllResponseObserver, never()).onError(any());
  }
}


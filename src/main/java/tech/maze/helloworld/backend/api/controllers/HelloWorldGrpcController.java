package tech.maze.helloworld.backend.api.controllers;

import io.grpc.stub.StreamObserver;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tech.maze.dtos.helloworld.controllers.HelloWorldGrpc;
import tech.maze.dtos.helloworld.payloads.GetAllRequest;
import tech.maze.dtos.helloworld.payloads.GetAllResponse;
import tech.maze.helloworld.backend.api.mappers.MessageDtoMapper;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.in.GetAllMessagesUseCase;

/**
 * A gRPC controller that handles requests for the Hello World service.
 */
@RequiredArgsConstructor
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HelloWorldGrpcController extends HelloWorldGrpc.HelloWorldImplBase {
  GetAllMessagesUseCase getAllMessagesUseCase;
  MessageDtoMapper messageDtoMapper;

  @Override
  public void getAll(
      GetAllRequest request,
      StreamObserver<GetAllResponse> responseObserver
  ) {
    final GetAllResponse response = GetAllResponse.newBuilder()
        .addAllMessages(getAllMessagesUseCase.getAllMessages()
          .stream()
          .map((Message message) -> messageDtoMapper.toDto(message))
          .collect(Collectors.toList()))
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}


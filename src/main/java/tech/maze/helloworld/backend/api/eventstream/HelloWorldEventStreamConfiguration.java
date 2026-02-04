package tech.maze.helloworld.backend.api.eventstream;

import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.maze.commons.eventstream.EventSender;
import tech.maze.commons.eventstream.MazeEventProperties;
import tech.maze.dtos.helloworld.events.EventTypes;
import tech.maze.dtos.helloworld.payloads.AddRequest;
import tech.maze.dtos.helloworld.payloads.AddResponse;
import tech.maze.helloworld.backend.api.mappers.AddRequestMapper;
import tech.maze.helloworld.backend.api.mappers.MessageDtoMapper;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.in.SaveMessageUseCase;

/**
 * Event stream configuration for Hello World event processing.
 */
@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EnableConfigurationProperties(MazeEventProperties.class)
@Slf4j
public class HelloWorldEventStreamConfiguration {
  EventSender eventSender;
  SaveMessageUseCase saveMessageUseCase;
  AddRequestMapper addRequestMapper;
  MessageDtoMapper messageDtoMapper;
  ObjectProvider<MeterRegistry> meterRegistryProvider;

  /**
   * Handles AddRequest events delivered via the event stream.
   *
   * @return a consumer for CloudEvents
   */
  @Bean
  public Consumer<CloudEvent> addRequestConsumer() {
    return event -> {
      if (!EventTypes.ADD_REQUEST.equals(event.getType())) {
        log.warn("Skipping event type {} (expected {})", event.getType(), EventTypes.ADD_REQUEST);
        return;
      }

      final AddRequest request = parseAddRequest(event);
      final Message message = addRequestMapper.toDomain(request);
      message.setCreatedAt(Instant.now());
      final Message savedMessage = saveMessageUseCase.saveMessage(message);

      final AddResponse response = AddResponse.newBuilder()
          .setMessage(messageDtoMapper.toDto(savedMessage))
          .build();

      final String replyTo = eventSender.resolveReplyTo(event);
      if (replyTo == null || replyTo.isBlank()) {
        return;
      }

      final boolean sent = eventSender.send(replyTo, response);
      if (!sent) {
        log.error("Failed to dispatch AddResponse for event {}", event.getId());
        final MeterRegistry registry = meterRegistryProvider.getIfAvailable();
        if (registry != null) {
          registry.counter(
              "maze.events.reply.failed",
              "eventType",
              EventTypes.ADD_REQUEST
          ).increment();
        }
      }
    };
  }

  private AddRequest parseAddRequest(CloudEvent event) {
    final byte[] bytes = extractBytes(event);

    try {
      return AddRequest.parseFrom(bytes);
    } catch (InvalidProtocolBufferException ex) {
      throw new IllegalArgumentException("Failed to decode AddRequest payload", ex);
    }
  }

  private byte[] extractBytes(CloudEvent event) {
    final CloudEventData data = event.getData();
    if (data == null) {
      throw new IllegalArgumentException("CloudEvent has no data");
    }

    return data.toBytes();
  }

}

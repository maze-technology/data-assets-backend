package tech.maze.helloworld.backend.api.eventstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.net.URI;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import tech.maze.commons.eventstream.EventSender;
import tech.maze.dtos.helloworld.events.EventTypes;
import tech.maze.dtos.helloworld.payloads.AddRequest;
import tech.maze.dtos.helloworld.payloads.AddResponse;
import tech.maze.helloworld.backend.api.mappers.AddRequestMapper;
import tech.maze.helloworld.backend.api.mappers.MessageDtoMapper;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.in.SaveMessageUseCase;

@ExtendWith(MockitoExtension.class)
class HelloWorldEventStreamConfigurationTest {
  @Mock
  private EventSender eventSender;

  @Mock
  private SaveMessageUseCase saveMessageUseCase;

  @Mock
  private AddRequestMapper addRequestMapper;

  @Mock
  private MessageDtoMapper messageDtoMapper;

  @Mock
  private ObjectProvider<io.micrometer.core.instrument.MeterRegistry> meterRegistryProvider;

  private HelloWorldEventStreamConfiguration configuration;
  private Consumer<CloudEvent> consumer;

  @BeforeEach
  void setUp() {
    configuration = new HelloWorldEventStreamConfiguration(
        eventSender,
        saveMessageUseCase,
        addRequestMapper,
        messageDtoMapper,
        meterRegistryProvider
    );
    consumer = configuration.addRequestConsumer();
  }

  @Test
  void skipsEventsWithUnexpectedType() {
    CloudEvent event = CloudEventBuilder.v1()
        .withId("evt-1")
        .withSource(URI.create("urn:test"))
        .withType("tech.maze.unexpected")
        .withData(BytesCloudEventData.wrap(new byte[] {1, 2, 3}))
        .build();

    consumer.accept(event);

    verify(addRequestMapper, never()).toDomain(any());
    verify(saveMessageUseCase, never()).saveMessage(any());
    verify(eventSender, never()).send(any(), any());
  }

  @Test
  void processesAddRequestWithoutReply() {
    AddRequest request = AddRequest.newBuilder().build();
    CloudEvent event = eventWithRequest(request);

    Message message = Message.builder().content("hello").build();
    Message saved = Message.builder().content("saved").build();

    when(addRequestMapper.toDomain(request)).thenReturn(message);
    when(saveMessageUseCase.saveMessage(message)).thenReturn(saved);
    when(messageDtoMapper.toDto(saved)).thenReturn(
        tech.maze.dtos.helloworld.models.Message.newBuilder().build()
    );
    when(eventSender.resolveReplyTo(event)).thenReturn(" ");

    consumer.accept(event);

    verify(saveMessageUseCase).saveMessage(message);
    verify(eventSender, never()).send(any(), any());
  }

  @Test
  void sendsReplyWhenReplyToPresent() {
    AddRequest request = AddRequest.newBuilder().build();
    CloudEvent event = eventWithRequest(request);

    Message message = Message.builder().content("hello").build();
    Message saved = Message.builder().content("saved").build();

    when(addRequestMapper.toDomain(request)).thenReturn(message);
    when(saveMessageUseCase.saveMessage(message)).thenReturn(saved);
    when(messageDtoMapper.toDto(saved)).thenReturn(
        tech.maze.dtos.helloworld.models.Message.newBuilder().build()
    );
    when(eventSender.resolveReplyTo(event)).thenReturn("reply-topic");
    when(eventSender.send(eq("reply-topic"), any(AddResponse.class))).thenReturn(true);

    consumer.accept(event);

    verify(eventSender).send(eq("reply-topic"), any(AddResponse.class));
  }

  @Test
  void recordsMetricWhenReplySendFails() {
    AddRequest request = AddRequest.newBuilder().build();
    CloudEvent event = eventWithRequest(request);

    Message message = Message.builder().content("hello").build();
    Message saved = Message.builder().content("saved").build();

    when(addRequestMapper.toDomain(request)).thenReturn(message);
    when(saveMessageUseCase.saveMessage(message)).thenReturn(saved);
    when(messageDtoMapper.toDto(saved)).thenReturn(
        tech.maze.dtos.helloworld.models.Message.newBuilder().build()
    );
    when(eventSender.resolveReplyTo(event)).thenReturn("reply-topic");
    when(eventSender.send(eq("reply-topic"), any(AddResponse.class))).thenReturn(false);

    SimpleMeterRegistry registry = new SimpleMeterRegistry();
    when(meterRegistryProvider.getIfAvailable()).thenReturn(registry);

    consumer.accept(event);

    assertEquals(
        1.0,
        registry.counter("maze.events.reply.failed", "eventType", EventTypes.ADD_REQUEST).count()
    );
  }

  @Test
  void rejectsEventsWithMissingData() {
    CloudEvent event = CloudEventBuilder.v1()
        .withId("evt-2")
        .withSource(URI.create("urn:test"))
        .withType(EventTypes.ADD_REQUEST)
        .build();

    assertThrows(IllegalArgumentException.class, () -> consumer.accept(event));
  }

  @Test
  void rejectsEventsWithInvalidPayload() {
    CloudEvent event = CloudEventBuilder.v1()
        .withId("evt-3")
        .withSource(URI.create("urn:test"))
        .withType(EventTypes.ADD_REQUEST)
        .withData(BytesCloudEventData.wrap("bad".getBytes()))
        .build();

    assertThrows(IllegalArgumentException.class, () -> consumer.accept(event));
  }

  private CloudEvent eventWithRequest(AddRequest request) {
    return CloudEventBuilder.v1()
        .withId("evt-add")
        .withSource(URI.create("urn:test"))
        .withType(EventTypes.ADD_REQUEST)
        .withData(BytesCloudEventData.wrap(request.toByteArray()))
        .build();
  }
}

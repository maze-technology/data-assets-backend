package tech.maze.data.assets.backend.api.eventstream;

import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.protobuf.ProtoCloudEventData;
import io.micrometer.core.instrument.MeterRegistry;
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

/**
 * Event stream configuration for assets processing.
 */
@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EnableConfigurationProperties(MazeEventProperties.class)
@Slf4j
public class AssetsEventStreamConfiguration {
  EventSender eventSender;
  AssetMetaDatasEventService assetMetaDatasEventService;
  ObjectProvider<MeterRegistry> meterRegistryProvider;

  /**
   * Handles FetchAssetsRequest events delivered via the event stream.
   *
   * @return a consumer for CloudEvents
   */
  @Bean
  public Consumer<CloudEvent> fetchAssetsRequestConsumer() {
    return event -> {
      if (!tech.maze.dtos.assets.events.EventTypes.FETCH_ASSETS_REQUEST.equals(event.getType())) {
        log.warn(
            "Skipping event type {} (expected {})",
            event.getType(),
            tech.maze.dtos.assets.events.EventTypes.FETCH_ASSETS_REQUEST
        );
        return;
      }

      sendReply(
          event,
          Empty.getDefaultInstance(),
          tech.maze.dtos.assets.events.EventTypes.FETCH_ASSETS_REQUEST
      );
    };
  }

  /**
   * Handles SyncAssetsRequest events delivered via the event stream.
   *
   * @return a consumer for CloudEvents
   */
  @Bean
  public Consumer<CloudEvent> syncAssetsRequestConsumer() {
    return event -> {
      if (!tech.maze.dtos.assets.events.EventTypes.SYNC_ASSETS_REQUEST.equals(event.getType())) {
        log.warn(
            "Skipping event type {} (expected {})",
            event.getType(),
            tech.maze.dtos.assets.events.EventTypes.SYNC_ASSETS_REQUEST
        );
        return;
      }

      sendReply(
          event,
          Empty.getDefaultInstance(),
          tech.maze.dtos.assets.events.EventTypes.SYNC_ASSETS_REQUEST
      );
    };
  }

  /**
   * Handles FetchAssetsResponse events delivered via the event stream.
   *
   * <p>One event carries one asset metadata payload.
   * </p>
   *
   * @return a consumer for CloudEvents
   */
  @Bean
  public Consumer<CloudEvent> fetchAssetsResponseConsumer() {
    return event -> {
      if (!tech.maze.dtos.assets.events.EventTypes.FETCH_ASSETS_RESPONSE.equals(event.getType())) {
        log.warn(
            "Skipping event type {} (expected {})",
            event.getType(),
            tech.maze.dtos.assets.events.EventTypes.FETCH_ASSETS_RESPONSE
        );
        return;
      }

      final tech.maze.dtos.assets.models.AssetMetaDatas payload = extractAssetMetaDatas(event);
      assetMetaDatasEventService.process(payload);
    };
  }

  private tech.maze.dtos.assets.models.AssetMetaDatas extractAssetMetaDatas(CloudEvent event) {
    final CloudEventData cloudEventData = event.getData();
    if (cloudEventData == null) {
      throw new IllegalArgumentException("CloudEvent payload is required");
    }

    try {
      if (cloudEventData instanceof ProtoCloudEventData protoCloudEventData) {
        final com.google.protobuf.Any any = protoCloudEventData.getAny();
        if (any.is(tech.maze.dtos.assets.models.AssetMetaDatas.class)) {
          return any.unpack(tech.maze.dtos.assets.models.AssetMetaDatas.class);
        }
        return tech.maze.dtos.assets.models.AssetMetaDatas.parseFrom(any.getValue());
      }

      return tech.maze.dtos.assets.models.AssetMetaDatas.parseFrom(cloudEventData.toBytes());
    } catch (InvalidProtocolBufferException ex) {
      throw new IllegalArgumentException("Invalid AssetMetaDatas payload", ex);
    }
  }

  private void sendReply(CloudEvent event, com.google.protobuf.Message response, String eventType) {
    final String replyTo = eventSender.resolveReplyTo(event);
    if (replyTo == null || replyTo.isBlank()) {
      return;
    }

    final boolean sent = eventSender.send(replyTo, response);
    if (!sent) {
      log.error("Failed to dispatch reply for event {}", event.getId());
      final MeterRegistry registry = meterRegistryProvider.getIfAvailable();
      if (registry != null) {
        registry.counter("maze.events.reply.failed", "eventType", eventType).increment();
      }
    }
  }

}

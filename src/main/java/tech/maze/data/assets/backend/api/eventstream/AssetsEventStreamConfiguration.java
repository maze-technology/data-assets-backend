package tech.maze.data.assets.backend.api.eventstream;

import com.google.protobuf.Empty;
import io.cloudevents.CloudEvent;
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

      sendReply(event, Empty.getDefaultInstance(), tech.maze.dtos.assets.events.EventTypes.FETCH_ASSETS_REQUEST);
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

      sendReply(event, Empty.getDefaultInstance(), tech.maze.dtos.assets.events.EventTypes.SYNC_ASSETS_REQUEST);
    };
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

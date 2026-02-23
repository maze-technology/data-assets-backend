package tech.maze.data.assets.backend.api.eventstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.protobuf.ProtoCloudEventData;
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

@ExtendWith(MockitoExtension.class)
class AssetsEventStreamConfigurationTest {
  @Mock
  private EventSender eventSender;
  @Mock
  private AssetMetaDatasEventService assetMetaDatasEventService;

  @Mock
  private ObjectProvider<io.micrometer.core.instrument.MeterRegistry> meterRegistryProvider;

  private AssetsEventStreamConfiguration configuration;

  @BeforeEach
  void setUp() {
    configuration = new AssetsEventStreamConfiguration(
        eventSender,
        assetMetaDatasEventService,
        meterRegistryProvider
    );
  }

  @Test
  void skipsUnexpectedTypeForFetchConsumer() {
    Consumer<CloudEvent> consumer = configuration.fetchAssetsRequestConsumer();
    CloudEvent event = CloudEventBuilder.v1()
        .withId("evt-1")
        .withSource(URI.create("urn:test"))
        .withType("unexpected")
        .withData(BytesCloudEventData.wrap(new byte[] {1}))
        .build();

    consumer.accept(event);

    verify(eventSender, never()).send(any(), any());
  }

  @Test
  void sendsReplyForFetchWhenReplyToPresent() {
    Consumer<CloudEvent> consumer = configuration.fetchAssetsRequestConsumer();
    CloudEvent event = CloudEventBuilder.v1()
        .withId("evt-fetch")
        .withSource(URI.create("urn:test"))
        .withType(tech.maze.dtos.assets.events.EventTypes.FETCH_ASSETS_REQUEST)
        .withData(BytesCloudEventData.wrap(com.google.protobuf.Empty.getDefaultInstance().toByteArray()))
        .build();

    when(eventSender.resolveReplyTo(event)).thenReturn("reply-topic");
    when(eventSender.send(eq("reply-topic"), any())).thenReturn(true);

    consumer.accept(event);

    verify(eventSender).send(eq("reply-topic"), any());
  }

  @Test
  void recordsMetricWhenSyncReplyFails() {
    Consumer<CloudEvent> consumer = configuration.syncAssetsRequestConsumer();
    CloudEvent event = CloudEventBuilder.v1()
        .withId("evt-sync")
        .withSource(URI.create("urn:test"))
        .withType(tech.maze.dtos.assets.events.EventTypes.SYNC_ASSETS_REQUEST)
        .withData(BytesCloudEventData.wrap(com.google.protobuf.Empty.getDefaultInstance().toByteArray()))
        .build();

    when(eventSender.resolveReplyTo(event)).thenReturn("reply-topic");
    when(eventSender.send(eq("reply-topic"), any())).thenReturn(false);

    SimpleMeterRegistry registry = new SimpleMeterRegistry();
    when(meterRegistryProvider.getIfAvailable()).thenReturn(registry);

    consumer.accept(event);

    assertEquals(
        1.0,
        registry.counter(
            "maze.events.reply.failed",
            "eventType",
            tech.maze.dtos.assets.events.EventTypes.SYNC_ASSETS_REQUEST
        ).count()
    );
  }

  @Test
  void processesFetchResponseWithSingleAssetMetaData() {
    Consumer<CloudEvent> consumer = configuration.fetchAssetsResponseConsumer();
    tech.maze.dtos.assets.models.AssetMetaDatas payload =
        tech.maze.dtos.assets.models.AssetMetaDatas.newBuilder()
            .setDataProviderId(com.google.protobuf.Value.newBuilder().setStringValue("11111111-1111-1111-1111-111111111111").build())
            .setAsset(
                tech.maze.dtos.assets.models.AssetMetaDatasAsset.newBuilder()
                    .setId("BTC")
                    .setSymbol("BTC")
                    .setName("Bitcoin")
                    .build()
            )
            .setPrimaryClass(tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO)
            .putExtraDatas("k", "v")
            .putToolBox("k", "v")
            .build();
    CloudEvent event = CloudEventBuilder.v1()
        .withId("evt-fetch-response")
        .withSource(URI.create("urn:test"))
        .withType(tech.maze.dtos.assets.events.EventTypes.FETCH_ASSETS_RESPONSE)
        .withData(ProtoCloudEventData.wrap(payload))
        .build();

    consumer.accept(event);

    verify(assetMetaDatasEventService).process(payload);
  }
}

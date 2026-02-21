package tech.maze.data.assets.backend.api.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Value;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CriterionValueExtractorTest {
  private final CriterionValueExtractor extractor = new CriterionValueExtractor();

  @Test
  void extractUuidReturnsUuidWhenValueContainsValidString() {
    final UUID id = UUID.randomUUID();
    final Value value = Value.newBuilder().setStringValue(id.toString()).build();

    assertThat(extractor.extractUuid(value)).contains(id);
  }

  @Test
  void extractUuidReturnsEmptyForInvalidInput() {
    assertThat(extractor.extractUuid(null)).isEmpty();
    assertThat(extractor.extractUuid(Value.newBuilder().setStringValue("not-a-uuid").build())).isEmpty();
  }

  @Test
  void extractUuidsKeepsOnlyValidDistinctValues() {
    final UUID id = UUID.randomUUID();

    final List<UUID> result = extractor.extractUuids(List.of(
        Value.newBuilder().setStringValue(id.toString()).build(),
        Value.newBuilder().setStringValue("not-a-uuid").build(),
        Value.newBuilder().setStringValue(id.toString()).build()
    ));

    assertThat(result).containsExactly(id);
  }
}

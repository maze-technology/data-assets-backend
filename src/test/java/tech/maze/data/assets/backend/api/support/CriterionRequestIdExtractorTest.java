package tech.maze.data.assets.backend.api.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Value;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CriterionRequestIdExtractorTest {
  private final CriterionRequestIdExtractor extractor = new CriterionRequestIdExtractor();

  @Test
  void extractsUuidFromFindOneRequest() {
    final UUID id = UUID.randomUUID();
    final var request = tech.maze.dtos.assets.requests.FindOneRequest.newBuilder()
        .setCriterion(
            tech.maze.dtos.assets.search.Criterion.newBuilder()
                .setFilter(
                    tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                        .setById(Value.newBuilder().setStringValue(id.toString()).build())
                        .build()
                )
                .build()
        )
        .build();

    assertThat(extractor.extractId(request)).isEqualTo(id);
  }

  @Test
  void returnsNullWhenUuidIsInvalid() {
    final var request = tech.maze.dtos.assets.requests.FindOneRequest.newBuilder()
        .setCriterion(
            tech.maze.dtos.assets.search.Criterion.newBuilder()
                .setFilter(
                    tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                        .setById(Value.newBuilder().setStringValue("not-a-uuid").build())
                        .build()
                )
                .build()
        )
        .build();

    assertThat(extractor.extractId(request)).isNull();
  }

  @Test
  void returnsNullWhenRequestIsNull() {
    assertThat(extractor.extractId(null)).isNull();
  }
}

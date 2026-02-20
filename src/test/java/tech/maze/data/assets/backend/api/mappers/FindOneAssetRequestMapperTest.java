package tech.maze.data.assets.backend.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Value;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FindOneAssetRequestMapperTest {
  private final FindOneAssetRequestMapper mapper = new FindOneAssetRequestMapper();

  @Test
  void toIdExtractsUuidFromFindOneRequest() {
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

    assertThat(mapper.toId(request)).isEqualTo(id);
  }

  @Test
  void extractIdFromCriterionRequestReturnsNullWhenRequestIsNull() {
    assertThat(mapper.extractIdFromCriterionRequest(null)).isNull();
  }

  @Test
  void extractIdFromCriterionRequestReturnsNullWhenCriterionIsMissing() {
    final var request = tech.maze.dtos.assets.requests.FindOneRequest.newBuilder().build();

    assertThat(mapper.extractIdFromCriterionRequest(request)).isNull();
  }

  @Test
  void extractIdFromCriterionRequestReturnsNullWhenUuidIsBlank() {
    final var request = tech.maze.dtos.assets.requests.FindOneRequest.newBuilder()
        .setCriterion(
            tech.maze.dtos.assets.search.Criterion.newBuilder()
                .setFilter(
                    tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                        .setById(Value.newBuilder().setStringValue(" ").build())
                        .build()
                )
                .build()
        )
        .build();

    assertThat(mapper.extractIdFromCriterionRequest(request)).isNull();
  }

  @Test
  void extractIdFromCriterionRequestReturnsNullWhenUuidIsInvalid() {
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

    assertThat(mapper.extractIdFromCriterionRequest(request)).isNull();
  }
}

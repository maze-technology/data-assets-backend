package tech.maze.data.assets.backend.api.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.protobuf.Value;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.api.support.CriterionValueExtractor;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;

@ExtendWith(MockitoExtension.class)
class FindOneAssetByIdSearchStrategyTest {
  @Mock
  private FindAssetUseCase findAssetUseCase;
  @Mock
  private Asset asset;

  @Test
  void supportsOnlyCriterionWithByIdStringValue() {
    final var strategy = new FindOneAssetByIdSearchStrategy(findAssetUseCase, new CriterionValueExtractor());
    final var valid = criterionWithId(UUID.randomUUID().toString());

    assertThat(strategy.supports(valid)).isTrue();
    assertThat(strategy.supports(null)).isFalse();
    assertThat(strategy.supports(tech.maze.dtos.assets.search.Criterion.newBuilder().build())).isFalse();
  }

  @Test
  void searchDelegatesToFindUseCaseForValidUuid() {
    final var strategy = new FindOneAssetByIdSearchStrategy(findAssetUseCase, new CriterionValueExtractor());
    final UUID id = UUID.randomUUID();
    final var criterion = criterionWithId(id.toString());
    when(findAssetUseCase.findById(id)).thenReturn(Optional.of(asset));

    final var result = strategy.search(criterion);

    assertThat(result).contains(asset);
    verify(findAssetUseCase).findById(id);
  }

  @Test
  void searchReturnsEmptyWhenUuidIsInvalid() {
    final var strategy = new FindOneAssetByIdSearchStrategy(findAssetUseCase, new CriterionValueExtractor());

    final var result = strategy.search(criterionWithId("not-a-uuid"));

    assertThat(result).isEmpty();
    verifyNoInteractions(findAssetUseCase);
  }

  private static tech.maze.dtos.assets.search.Criterion criterionWithId(String id) {
    return tech.maze.dtos.assets.search.Criterion.newBuilder()
        .setFilter(
            tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                .setById(Value.newBuilder().setStringValue(id).build())
                .build()
        )
        .build();
  }
}

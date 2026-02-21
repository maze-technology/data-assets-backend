package tech.maze.data.assets.backend.api.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.domain.models.Asset;

@ExtendWith(MockitoExtension.class)
class FindOneAssetSearchStrategyHandlerTest {
  @Mock
  private FindOneAssetSearchStrategy first;
  @Mock
  private FindOneAssetSearchStrategy second;
  @Mock
  private Asset asset;

  @Test
  void usesFirstSupportingStrategy() {
    final var criterion = tech.maze.dtos.assets.search.Criterion.newBuilder().build();
    final var handler = new FindOneAssetSearchStrategyHandler(List.of(first, second));
    when(first.supports(criterion)).thenReturn(false);
    when(second.supports(criterion)).thenReturn(true);
    when(second.search(criterion)).thenReturn(Optional.of(asset));

    final var result = handler.handleSearch(criterion);

    assertThat(result).contains(asset);
    verify(first).supports(criterion);
    verify(second).supports(criterion);
    verify(second).search(criterion);
  }

  @Test
  void returnsEmptyWhenNoStrategySupportsCriterion() {
    final var criterion = tech.maze.dtos.assets.search.Criterion.newBuilder().build();
    final var handler = new FindOneAssetSearchStrategyHandler(List.of(first, second));
    when(first.supports(criterion)).thenReturn(false);
    when(second.supports(criterion)).thenReturn(false);

    final var result = handler.handleSearch(criterion);

    assertThat(result).isEmpty();
    verify(first).supports(criterion);
    verify(second).supports(criterion);
    verify(first, never()).search(criterion);
    verify(second, never()).search(criterion);
  }
}

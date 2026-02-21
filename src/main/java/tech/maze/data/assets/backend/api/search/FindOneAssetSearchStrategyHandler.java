package tech.maze.data.assets.backend.api.search;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.dtos.assets.search.Criterion;

/**
 * Delegates FindOne asset lookup to the first matching strategy.
 */
@Service
@RequiredArgsConstructor
public class FindOneAssetSearchStrategyHandler {
  private final List<FindOneAssetSearchStrategy> strategies;

  /**
   * Resolves a single asset from criterion using available strategies.
   */
  public Optional<Asset> handleSearch(Criterion criterion) {
    return strategies.stream()
        .filter(strategy -> strategy.supports(criterion))
        .findFirst()
        .flatMap(strategy -> strategy.search(criterion));
  }
}

package tech.maze.data.assets.backend.api.search;

import java.util.Optional;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.dtos.assets.search.Criterion;

/**
 * Strategy for resolving a single asset from a request criterion.
 */
public interface FindOneAssetSearchStrategy {
  /**
   * Whether this strategy supports the given criterion.
   */
  boolean supports(Criterion criterion);

  /**
   * Attempts to resolve an asset for the criterion.
   */
  Optional<Asset> search(Criterion criterion);
}

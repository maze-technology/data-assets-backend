package tech.maze.data.assets.backend.domain.ports.in;

import java.util.List;
import java.util.UUID;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.AssetsPage;

/**
 * Generated type.
 */
public interface SearchAssetsUseCase {
  /**
   * Generated method.
   */
  List<Asset> findAll();

  /**
   * Generated method.
   */
  AssetsPage findByDataProviderIds(List<UUID> dataProviderIds, int page, int limit);
}

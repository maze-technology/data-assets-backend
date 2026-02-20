package tech.maze.data.assets.backend.domain.ports.in;

import java.util.List;
import java.util.UUID;
import tech.maze.data.assets.backend.domain.models.Asset;

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
  List<Asset> findByDataProviderIds(List<UUID> dataProviderIds);
}

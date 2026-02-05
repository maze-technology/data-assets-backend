package tech.maze.data.assets.backend.domain.ports.in;

import java.util.Optional;
import java.util.UUID;
import tech.maze.data.assets.backend.domain.models.Asset;

/**
 * Generated type.
 */
public interface FindAssetUseCase {
  /**
   * Generated method.
   */
  Optional<Asset> findById(UUID id);
}

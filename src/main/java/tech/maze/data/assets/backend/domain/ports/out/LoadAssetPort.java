package tech.maze.data.assets.backend.domain.ports.out;

import java.util.Optional;
import java.util.UUID;
import tech.maze.data.assets.backend.domain.models.Asset;

public interface LoadAssetPort {
  Optional<Asset> findById(UUID id);
}

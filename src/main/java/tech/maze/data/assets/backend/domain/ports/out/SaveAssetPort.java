package tech.maze.data.assets.backend.domain.ports.out;

import tech.maze.data.assets.backend.domain.models.Asset;

public interface SaveAssetPort {
  Asset save(Asset asset);
}

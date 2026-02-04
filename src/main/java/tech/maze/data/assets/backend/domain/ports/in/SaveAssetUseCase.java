package tech.maze.data.assets.backend.domain.ports.in;

import tech.maze.data.assets.backend.domain.models.Asset;

public interface SaveAssetUseCase {
  Asset save(Asset asset);
}

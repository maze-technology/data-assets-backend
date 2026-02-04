package tech.maze.data.assets.backend.domain.ports.in;

import java.util.List;
import tech.maze.data.assets.backend.domain.models.Asset;

public interface SearchAssetsUseCase {
  List<Asset> findAll();
}

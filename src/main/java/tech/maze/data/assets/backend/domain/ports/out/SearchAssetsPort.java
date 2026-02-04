package tech.maze.data.assets.backend.domain.ports.out;

import java.util.List;
import tech.maze.data.assets.backend.domain.models.Asset;

public interface SearchAssetsPort {
  List<Asset> findAll();
}

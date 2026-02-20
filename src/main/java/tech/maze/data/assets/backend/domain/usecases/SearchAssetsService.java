package tech.maze.data.assets.backend.domain.usecases;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.SearchAssetsUseCase;
import tech.maze.data.assets.backend.domain.ports.out.SearchAssetsPort;

/**
 * Use case for listing assets.
 */
@Service
@RequiredArgsConstructor
public class SearchAssetsService implements SearchAssetsUseCase {
  private final SearchAssetsPort searchAssetsPort;

  @Override
  public List<Asset> findAll() {
    return searchAssetsPort.findAll();
  }

  @Override
  public List<Asset> findByDataProviderIds(List<UUID> dataProviderIds) {
    return searchAssetsPort.findByDataProviderIds(dataProviderIds);
  }
}

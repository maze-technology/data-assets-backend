package tech.maze.data.assets.backend.domain.usecases;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.AssetsPage;
import tech.maze.data.assets.backend.domain.ports.in.SearchAssetsUseCase;
import tech.maze.data.assets.backend.domain.ports.out.SearchAssetsPort;

/**
 * Use case for listing assets.
 */
@Service
@RequiredArgsConstructor
public class SearchAssetsUseCaseImpl implements SearchAssetsUseCase {
  private final SearchAssetsPort searchAssetsPort;

  @Override
  public List<Asset> findAll() {
    return searchAssetsPort.findAll();
  }

  @Override
  public AssetsPage findByDataProviderIds(List<UUID> dataProviderIds, int page, int limit) {
    return searchAssetsPort.findByDataProviderIds(dataProviderIds, page, limit);
  }
}

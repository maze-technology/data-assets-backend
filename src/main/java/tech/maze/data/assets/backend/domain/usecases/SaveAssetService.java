package tech.maze.data.assets.backend.domain.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.SaveAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.out.SaveAssetPort;

/**
 * Use case for saving assets.
 */
@Service
@RequiredArgsConstructor
public class SaveAssetService implements SaveAssetUseCase {
  private final SaveAssetPort saveAssetPort;

  @Override
  public Asset save(Asset asset) {
    return saveAssetPort.save(asset);
  }
}

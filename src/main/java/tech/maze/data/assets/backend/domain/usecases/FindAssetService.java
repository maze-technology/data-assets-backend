package tech.maze.data.assets.backend.domain.usecases;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;

@Service
@RequiredArgsConstructor
/**
 * Generated type.
 */
public class FindAssetService implements FindAssetUseCase {
  private final LoadAssetPort loadAssetPort;

  @Override
  public Optional<Asset> findById(UUID id) {
    return loadAssetPort.findById(id);
  }
}

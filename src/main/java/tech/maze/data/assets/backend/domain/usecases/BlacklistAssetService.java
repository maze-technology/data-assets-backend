package tech.maze.data.assets.backend.domain.usecases;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.BlacklistAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;
import tech.maze.data.assets.backend.domain.ports.out.SaveAssetPort;

/**
 * Service implementing asset blacklist use case.
 */
@Service
@RequiredArgsConstructor
public class BlacklistAssetService implements BlacklistAssetUseCase {
  private final LoadAssetPort loadAssetPort;
  private final SaveAssetPort saveAssetPort;

  @Override
  public void blacklist(UUID id) {
    if (id == null) {
      return;
    }

    loadAssetPort.findById(id).ifPresent(asset -> saveAssetPort.save(toBlacklisted(asset)));
  }

  private static Asset toBlacklisted(Asset asset) {
    return new Asset(
        asset.id(),
        asset.symbol(),
        asset.name(),
        asset.primaryClass(),
        asset.createdAt(),
        true
    );
  }
}

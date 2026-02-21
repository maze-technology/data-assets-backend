package tech.maze.data.assets.backend.domain.usecases;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.BlacklistAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;
import tech.maze.data.assets.backend.domain.ports.out.SaveAssetPort;

/**
 * Blacklist asset use case implementation.
 */
@Service
@RequiredArgsConstructor
public class BlacklistAssetUseCaseImpl implements BlacklistAssetUseCase {
  private final LoadAssetPort loadAssetPort;
  private final SaveAssetPort saveAssetPort;

  @Override
  public void blacklist(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("id is required");
    }

    final Asset asset = loadAssetPort.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Asset not found"));

    saveAssetPort.save(toBlacklisted(asset));
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

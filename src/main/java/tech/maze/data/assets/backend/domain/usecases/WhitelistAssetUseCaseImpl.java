package tech.maze.data.assets.backend.domain.usecases;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.WhitelistAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;
import tech.maze.data.assets.backend.domain.ports.out.SaveAssetPort;

/**
 * Whitelist asset use case implementation.
 */
@Service
@RequiredArgsConstructor
public class WhitelistAssetUseCaseImpl implements WhitelistAssetUseCase {
  private final LoadAssetPort loadAssetPort;
  private final SaveAssetPort saveAssetPort;

  @Override
  public void whitelist(UUID id) {
    if (id == null) {
      return;
    }

    loadAssetPort.findById(id).ifPresent(asset -> saveAssetPort.save(toWhitelisted(asset)));
  }

  private static Asset toWhitelisted(Asset asset) {
    return new Asset(
        asset.id(),
        asset.symbol(),
        asset.name(),
        asset.primaryClass(),
        asset.createdAt(),
        false
    );
  }
}

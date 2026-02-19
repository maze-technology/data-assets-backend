package tech.maze.data.assets.backend.domain.ports.in;

import java.util.UUID;

/**
 * Use case for whitelisting assets.
 */
public interface WhitelistAssetUseCase {
  /**
   * Whitelists an asset by id.
   */
  void whitelist(UUID id);
}

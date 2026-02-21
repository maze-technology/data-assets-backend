package tech.maze.data.assets.backend.domain.ports.in;

import java.util.UUID;

/**
 * Use case for blacklisting assets.
 */
public interface BlacklistAssetUseCase {
  /**
   * Blacklists an asset by id.
   */
  void blacklist(UUID id);
}

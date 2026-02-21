package tech.maze.data.assets.backend.domain.ports.in;

import java.util.Optional;
import java.util.UUID;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;

/**
 * Generated type.
 */
public interface FindAssetUseCase {
  /**
   * Generated method.
   */
  Optional<Asset> findById(UUID id);

  /**
   * Generated method.
   */
  Optional<Asset> findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
      String symbol,
      String name,
      PrimaryClass primaryClass
  );

  /**
   * Generated method.
   */
  Optional<Asset> findByDataProviderIdAndDataProviderMetaDatasAssetId(
      UUID dataProviderId,
      String dataProviderMetaDatasAssetId
  );

  /**
   * Generated method.
   */
  Optional<Asset> findByDataProviderIdAndDataProviderSymbol(
      UUID dataProviderId,
      String symbol
  );
}

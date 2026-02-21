package tech.maze.data.assets.backend.infrastructure.persistence.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.maze.data.assets.backend.infrastructure.persistence.entities.AssetDataProviderMetaDataEntity;

/**
 * JPA repository for asset data-provider metadata.
 */
@Repository
public interface AssetDataProvidersMetadatasJpaRepository
    extends JpaRepository<AssetDataProviderMetaDataEntity, Long> {
  /**
   * Finds one metadata record by data provider and provider-side asset id.
   */
  Optional<AssetDataProviderMetaDataEntity> findFirstByDataProviderIdAndProviderAssetId(
      UUID dataProviderId,
      String providerAssetId
  );
}

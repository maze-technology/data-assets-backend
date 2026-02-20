package tech.maze.data.assets.backend.infrastructure.persistence.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.infrastructure.persistence.entities.AssetEntity;

/**
 * JPA repository for assets.
 */
@Repository
public interface AssetJpaRepository extends JpaRepository<AssetEntity, UUID> {
  /**
   * Finds one asset by unique business key.
   */
  java.util.Optional<AssetEntity> findFirstBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
      String symbol,
      String name,
      PrimaryClass primaryClass
  );

  /**
   * Finds one asset by provider and provider-side asset id metadata.
   */
  @Query(
      """
          SELECT DISTINCT a
          FROM AssetEntity a
          JOIN a.dataProvidersMetaDatas d
          WHERE d.dataProviderId = :dataProviderId
            AND d.providerAssetId = :dataProviderMetaDatasAssetId
          """
  )
  java.util.Optional<AssetEntity> findFirstByDataProviderIdAndDataProviderMetaDatasAssetId(
      @Param("dataProviderId") UUID dataProviderId,
      @Param("dataProviderMetaDatasAssetId") String dataProviderMetaDatasAssetId
  );

  /**
   * Finds one asset by provider and provider-side symbol metadata.
   */
  @Query(
      """
          SELECT DISTINCT a
          FROM AssetEntity a
          JOIN a.dataProvidersMetaDatas d
          WHERE d.dataProviderId = :dataProviderId
            AND LOWER(d.symbol) = LOWER(:symbol)
          """
  )
  java.util.Optional<AssetEntity> findFirstByDataProviderIdAndDataProviderSymbol(
      @Param("dataProviderId") UUID dataProviderId,
      @Param("symbol") String symbol
  );

  /**
   * Finds assets linked to any data provider ids.
   */
  @Query(
      """
          SELECT DISTINCT a
          FROM AssetEntity a
          JOIN a.dataProvidersMetaDatas d
          WHERE d.dataProviderId IN :dataProviderIds
          """
  )
  java.util.List<AssetEntity> findAllByDataProviderIds(
      @Param("dataProviderIds") java.util.List<UUID> dataProviderIds
  );
}

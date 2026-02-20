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
      value = """
          SELECT a.*
          FROM assets a
          WHERE EXISTS (
            SELECT 1
            FROM jsonb_array_elements(
                COALESCE(a.data_providers_meta_datas::jsonb, '[]'::jsonb)
            ) AS metadata
            WHERE COALESCE(
                metadata ->> 'dataProviderId',
                metadata ->> 'data_provider_id'
            ) = CAST(:dataProviderId AS text)
              AND metadata -> 'asset' ->> 'id' = :dataProviderMetaDatasAssetId
          )
          LIMIT 1
          """,
      nativeQuery = true
  )
  java.util.Optional<AssetEntity> findFirstByDataProviderIdAndDataProviderMetaDatasAssetId(
      @Param("dataProviderId") UUID dataProviderId,
      @Param("dataProviderMetaDatasAssetId") String dataProviderMetaDatasAssetId
  );

  /**
   * Finds one asset by provider and provider-side symbol metadata.
   */
  @Query(
      value = """
          SELECT a.*
          FROM assets a
          WHERE EXISTS (
            SELECT 1
            FROM jsonb_array_elements(
                COALESCE(a.data_providers_meta_datas::jsonb, '[]'::jsonb)
            ) AS metadata
            WHERE COALESCE(
                metadata ->> 'dataProviderId',
                metadata ->> 'data_provider_id'
            ) = CAST(:dataProviderId AS text)
              AND LOWER(metadata -> 'asset' ->> 'symbol') = LOWER(:symbol)
          )
          LIMIT 1
          """,
      nativeQuery = true
  )
  java.util.Optional<AssetEntity> findFirstByDataProviderIdAndDataProviderSymbol(
      @Param("dataProviderId") UUID dataProviderId,
      @Param("symbol") String symbol
  );

  /**
   * Finds assets linked to any data provider ids.
   */
  @Query(
      value = """
          SELECT DISTINCT a.*
          FROM assets a
          WHERE EXISTS (
            SELECT 1
            FROM jsonb_array_elements(
                COALESCE(a.data_providers_meta_datas::jsonb, '[]'::jsonb)
            ) AS metadata
            WHERE COALESCE(
                metadata ->> 'dataProviderId',
                metadata ->> 'data_provider_id'
            ) IN (:dataProviderIds)
          )
          """,
      nativeQuery = true
  )
  java.util.List<AssetEntity> findAllByDataProviderIds(
      @Param("dataProviderIds") java.util.List<String> dataProviderIds
  );
}

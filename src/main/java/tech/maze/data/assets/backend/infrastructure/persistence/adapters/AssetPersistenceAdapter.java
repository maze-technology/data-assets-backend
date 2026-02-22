package tech.maze.data.assets.backend.infrastructure.persistence.adapters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.AssetsPage;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;
import tech.maze.data.assets.backend.domain.ports.out.SaveAssetPort;
import tech.maze.data.assets.backend.domain.ports.out.SearchAssetsPort;
import tech.maze.data.assets.backend.infrastructure.persistence.entities.AssetEntity;
import tech.maze.data.assets.backend.infrastructure.persistence.mappers.AssetEntityMapper;
import tech.maze.data.assets.backend.infrastructure.persistence.repositories.AssetJpaRepository;

/**
 * Persistence adapter for assets.
 */
@Component
@RequiredArgsConstructor
public class AssetPersistenceAdapter implements LoadAssetPort, SaveAssetPort, SearchAssetsPort {
  private final AssetJpaRepository assetJpaRepository;
  private final AssetEntityMapper assetEntityMapper;

  @Override
  public Optional<Asset> findById(UUID id) {
    return assetJpaRepository.findById(id).map(assetEntityMapper::toDomain);
  }

  @Override
  public Optional<Asset> findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
      String symbol,
      String name,
      PrimaryClass primaryClass
  ) {
    return assetJpaRepository
        .findFirstBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(symbol, name, primaryClass)
        .map(assetEntityMapper::toDomain);
  }

  @Override
  public Optional<Asset> findByDataProviderIdAndDataProviderMetaDatasAssetId(
      UUID dataProviderId,
      String dataProviderMetaDatasAssetId
  ) {
    return assetJpaRepository
        .findFirstByDataProviderIdAndDataProviderMetaDatasAssetId(
            dataProviderId,
            dataProviderMetaDatasAssetId
        )
        .map(assetEntityMapper::toDomain);
  }

  @Override
  public Optional<Asset> findByDataProviderIdAndDataProviderSymbol(
      UUID dataProviderId,
      String symbol
  ) {
    return assetJpaRepository
        .findFirstByDataProviderIdAndDataProviderSymbol(dataProviderId, symbol)
        .map(assetEntityMapper::toDomain);
  }

  @Override
  public List<Asset> findAll() {
    return assetJpaRepository.findAll().stream().map(assetEntityMapper::toDomain).toList();
  }

  @Override
  public AssetsPage findByDataProviderIds(List<UUID> dataProviderIds, int page, int limit) {
    if (dataProviderIds == null || dataProviderIds.isEmpty()) {
      return new AssetsPage(List.of(), 0, 0);
    }

    final Page<AssetEntity> results =
        assetJpaRepository.findAllByDataProviderIds(dataProviderIds, PageRequest.of(page, limit));
    final List<Asset> assets = results.getContent().stream()
        .map(assetEntityMapper::toDomain)
        .toList();
    return new AssetsPage(assets, results.getTotalElements(), results.getTotalPages());
  }

  @Override
  public Asset save(Asset asset) {
    return assetEntityMapper.toDomain(assetJpaRepository.save(assetEntityMapper.toEntity(asset)));
  }
}

package tech.maze.data.assets.backend.infrastructure.persistence.adapters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;
import tech.maze.data.assets.backend.domain.ports.out.SaveAssetPort;
import tech.maze.data.assets.backend.domain.ports.out.SearchAssetsPort;
import tech.maze.data.assets.backend.infrastructure.persistence.mappers.AssetEntityMapper;
import tech.maze.data.assets.backend.infrastructure.persistence.repositories.AssetJpaRepository;

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
  public List<Asset> findAll() {
    return assetJpaRepository.findAll().stream().map(assetEntityMapper::toDomain).toList();
  }

  @Override
  public Asset save(Asset asset) {
    return assetEntityMapper.toDomain(assetJpaRepository.save(assetEntityMapper.toEntity(asset)));
  }
}

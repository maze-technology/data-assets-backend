package tech.maze.data.assets.backend.infrastructure.persistence.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.infrastructure.persistence.entities.AssetEntity;
import tech.maze.data.assets.backend.infrastructure.persistence.mappers.AssetEntityMapper;
import tech.maze.data.assets.backend.infrastructure.persistence.repositories.AssetJpaRepository;

@ExtendWith(MockitoExtension.class)
class AssetPersistenceAdapterTest {
  @Mock
  private AssetJpaRepository assetJpaRepository;
  @Mock
  private AssetEntityMapper assetEntityMapper;
  @Mock
  private AssetEntity entity;
  @Mock
  private Asset asset;

  @Test
  void findByIdMapsEntityToDomain() {
    final UUID id = UUID.randomUUID();
    when(assetJpaRepository.findById(id)).thenReturn(Optional.of(entity));
    when(assetEntityMapper.toDomain(entity)).thenReturn(asset);

    final var adapter = new AssetPersistenceAdapter(assetJpaRepository, assetEntityMapper);
    final var result = adapter.findById(id);

    assertThat(result).contains(asset);
    verify(assetJpaRepository).findById(id);
  }

  @Test
  void findAllMapsEntitiesToDomain() {
    when(assetJpaRepository.findAll()).thenReturn(List.of(entity));
    when(assetEntityMapper.toDomain(entity)).thenReturn(asset);

    final var adapter = new AssetPersistenceAdapter(assetJpaRepository, assetEntityMapper);
    final var result = adapter.findAll();

    assertThat(result).containsExactly(asset);
    verify(assetJpaRepository).findAll();
  }

  @Test
  void findByDataProviderIdsReturnsEmptyWhenInputIsEmpty() {
    final var adapter = new AssetPersistenceAdapter(assetJpaRepository, assetEntityMapper);

    final var result = adapter.findByDataProviderIds(List.of());

    assertThat(result).isEmpty();
  }

  @Test
  void findByDataProviderIdsDelegatesToRepository() {
    final UUID dataProviderId = UUID.randomUUID();
    when(assetJpaRepository.findAllByDataProviderIds(List.of(dataProviderId))).thenReturn(List.of(entity));
    when(assetEntityMapper.toDomain(entity)).thenReturn(asset);

    final var adapter = new AssetPersistenceAdapter(assetJpaRepository, assetEntityMapper);
    final var result = adapter.findByDataProviderIds(List.of(dataProviderId));

    assertThat(result).containsExactly(asset);
    verify(assetJpaRepository).findAllByDataProviderIds(List.of(dataProviderId));
  }

  @Test
  void findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClassDelegatesToRepository() {
    when(assetJpaRepository.findFirstBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
        "BTC",
        "Bitcoin",
        PrimaryClass.CRYPTO
    )).thenReturn(Optional.of(entity));
    when(assetEntityMapper.toDomain(entity)).thenReturn(asset);

    final var adapter = new AssetPersistenceAdapter(assetJpaRepository, assetEntityMapper);
    final var result = adapter.findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
        "BTC",
        "Bitcoin",
        PrimaryClass.CRYPTO
    );

    assertThat(result).contains(asset);
  }

  @Test
  void saveMapsDomainToEntityAndBack() {
    when(assetEntityMapper.toEntity(asset)).thenReturn(entity);
    when(assetJpaRepository.save(entity)).thenReturn(entity);
    when(assetEntityMapper.toDomain(entity)).thenReturn(asset);

    final var adapter = new AssetPersistenceAdapter(assetJpaRepository, assetEntityMapper);
    final var result = adapter.save(asset);

    assertThat(result).isSameAs(asset);
    verify(assetEntityMapper).toEntity(asset);
    verify(assetJpaRepository).save(entity);
    verify(assetEntityMapper).toDomain(entity);
  }
}

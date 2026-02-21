package tech.maze.data.assets.backend.domain.usecases;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;

/**
 * Use case for loading an asset by id.
 */
@Service
@RequiredArgsConstructor
public class FindAssetUseCaseImpl implements FindAssetUseCase {
  private final LoadAssetPort loadAssetPort;

  @Override
  public Optional<Asset> findById(UUID id) {
    return loadAssetPort.findById(id);
  }

  @Override
  public Optional<Asset> findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
      String symbol,
      String name,
      PrimaryClass primaryClass
  ) {
    return loadAssetPort.findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
        symbol,
        name,
        primaryClass
    );
  }

  @Override
  public Optional<Asset> findByDataProviderIdAndDataProviderMetaDatasAssetId(
      UUID dataProviderId,
      String dataProviderMetaDatasAssetId
  ) {
    return loadAssetPort.findByDataProviderIdAndDataProviderMetaDatasAssetId(
        dataProviderId,
        dataProviderMetaDatasAssetId
    );
  }

  @Override
  public Optional<Asset> findByDataProviderIdAndDataProviderSymbol(
      UUID dataProviderId,
      String symbol
  ) {
    return loadAssetPort.findByDataProviderIdAndDataProviderSymbol(dataProviderId, symbol);
  }
}

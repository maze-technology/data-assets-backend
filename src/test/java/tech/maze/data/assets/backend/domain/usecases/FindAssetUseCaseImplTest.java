package tech.maze.data.assets.backend.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;

@ExtendWith(MockitoExtension.class)
class FindAssetUseCaseImplTest {
  @Mock
  private LoadAssetPort loadAssetPort;
  @Mock
  private Asset asset;

  @Test
  void delegatesFindById() {
    final UUID id = UUID.randomUUID();
    when(loadAssetPort.findById(id)).thenReturn(Optional.of(asset));

    final var service = new FindAssetUseCaseImpl(loadAssetPort);
    final var result = service.findById(id);

    assertThat(result).contains(asset);
    verify(loadAssetPort).findById(id);
  }

  @Test
  void delegatesFindBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass() {
    when(loadAssetPort.findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass("BTC", "Bitcoin", PrimaryClass.CRYPTO))
        .thenReturn(Optional.of(asset));

    final var service = new FindAssetUseCaseImpl(loadAssetPort);
    final var result = service.findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
        "BTC",
        "Bitcoin",
        PrimaryClass.CRYPTO
    );

    assertThat(result).contains(asset);
    verify(loadAssetPort).findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass("BTC", "Bitcoin", PrimaryClass.CRYPTO);
  }

  @Test
  void delegatesFindByDataProviderIdAndDataProviderMetaDatasAssetId() {
    final UUID dataProviderId = UUID.randomUUID();
    when(loadAssetPort.findByDataProviderIdAndDataProviderMetaDatasAssetId(dataProviderId, "BTC"))
        .thenReturn(Optional.of(asset));

    final var service = new FindAssetUseCaseImpl(loadAssetPort);
    final var result = service.findByDataProviderIdAndDataProviderMetaDatasAssetId(dataProviderId, "BTC");

    assertThat(result).contains(asset);
    verify(loadAssetPort).findByDataProviderIdAndDataProviderMetaDatasAssetId(dataProviderId, "BTC");
  }

  @Test
  void delegatesFindByDataProviderIdAndDataProviderSymbol() {
    final UUID dataProviderId = UUID.randomUUID();
    when(loadAssetPort.findByDataProviderIdAndDataProviderSymbol(dataProviderId, "BTC"))
        .thenReturn(Optional.of(asset));

    final var service = new FindAssetUseCaseImpl(loadAssetPort);
    final var result = service.findByDataProviderIdAndDataProviderSymbol(dataProviderId, "BTC");

    assertThat(result).contains(asset);
    verify(loadAssetPort).findByDataProviderIdAndDataProviderSymbol(dataProviderId, "BTC");
  }
}

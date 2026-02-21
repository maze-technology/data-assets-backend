package tech.maze.data.assets.backend.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.AssetsPage;
import tech.maze.data.assets.backend.domain.ports.out.SearchAssetsPort;

@ExtendWith(MockitoExtension.class)
class SearchAssetsUseCaseImplTest {
  @Mock
  private SearchAssetsPort searchAssetsPort;
  @Mock
  private Asset asset;

  @Test
  void delegatesFindAll() {
    when(searchAssetsPort.findAll()).thenReturn(List.of(asset));

    final var service = new SearchAssetsUseCaseImpl(searchAssetsPort);
    final var result = service.findAll();

    assertThat(result).containsExactly(asset);
    verify(searchAssetsPort).findAll();
  }

  @Test
  void delegatesFindByDataProviderIds() {
    final UUID dataProviderId = UUID.randomUUID();
    final AssetsPage expected = new AssetsPage(List.of(asset), 1, 1);
    when(searchAssetsPort.findByDataProviderIds(List.of(dataProviderId), 0, 50))
        .thenReturn(expected);

    final var service = new SearchAssetsUseCaseImpl(searchAssetsPort);
    final var result = service.findByDataProviderIds(List.of(dataProviderId), 0, 50);

    assertThat(result).isEqualTo(expected);
    verify(searchAssetsPort).findByDataProviderIds(List.of(dataProviderId), 0, 50);
  }
}

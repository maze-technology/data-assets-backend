package tech.maze.data.assets.backend.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.out.SaveAssetPort;

@ExtendWith(MockitoExtension.class)
class SaveAssetServiceTest {
  @Mock
  private SaveAssetPort saveAssetPort;
  @Mock
  private Asset asset;

  @Test
  void delegatesSave() {
    when(saveAssetPort.save(asset)).thenReturn(asset);

    final var service = new SaveAssetService(saveAssetPort);
    final var result = service.save(asset);

    assertThat(result).isSameAs(asset);
    verify(saveAssetPort).save(asset);
  }
}

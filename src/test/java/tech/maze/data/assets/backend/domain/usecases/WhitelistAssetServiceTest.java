package tech.maze.data.assets.backend.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.domain.ports.out.LoadAssetPort;
import tech.maze.data.assets.backend.domain.ports.out.SaveAssetPort;

@ExtendWith(MockitoExtension.class)
class WhitelistAssetUseCaseImplTest {
  @Mock
  private LoadAssetPort loadAssetPort;
  @Mock
  private SaveAssetPort saveAssetPort;

  @Test
  void whitelistsWhenAssetExists() {
    final UUID id = UUID.randomUUID();
    final Asset asset = new Asset(id, "BTC", "Bitcoin", PrimaryClass.CRYPTO, Instant.now(), true);
    when(loadAssetPort.findById(id)).thenReturn(Optional.of(asset));

    final var service = new WhitelistAssetUseCaseImpl(loadAssetPort, saveAssetPort);
    service.whitelist(id);

    final ArgumentCaptor<Asset> captor = ArgumentCaptor.forClass(Asset.class);
    verify(saveAssetPort).save(captor.capture());
    assertThat(captor.getValue().blacklisted()).isFalse();
  }

  @Test
  void throwsWhenIdIsNull() {
    final var service = new WhitelistAssetUseCaseImpl(loadAssetPort, saveAssetPort);
    assertThatThrownBy(() -> service.whitelist(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("id is required");

    verify(loadAssetPort, never()).findById(any());
    verify(saveAssetPort, never()).save(any());
  }

  @Test
  void throwsWhenAssetNotFound() {
    final UUID id = UUID.randomUUID();
    when(loadAssetPort.findById(id)).thenReturn(Optional.empty());
    final var service = new WhitelistAssetUseCaseImpl(loadAssetPort, saveAssetPort);

    assertThatThrownBy(() -> service.whitelist(id))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Asset not found");

    verify(saveAssetPort, never()).save(any());
  }
}

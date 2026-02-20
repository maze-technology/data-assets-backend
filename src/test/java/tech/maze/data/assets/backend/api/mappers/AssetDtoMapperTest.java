package tech.maze.data.assets.backend.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Value;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;

class AssetDtoMapperTest {
  private final AssetDtoMapper mapper = new AssetDtoMapper() {
    @Override
    public tech.maze.dtos.assets.models.Asset toDto(Asset asset) {
      return null;
    }
  };

  @Test
  void uuidToValueReturnsDefaultForNull() {
    assertThat(mapper.uuidToValue(null)).isEqualTo(Value.getDefaultInstance());
  }

  @Test
  void uuidToValueReturnsStringValueForUuid() {
    final UUID id = UUID.randomUUID();

    assertThat(mapper.uuidToValue(id).getStringValue()).isEqualTo(id.toString());
  }

  @Test
  void primaryClassToDtoHandlesNullAndKnownValues() {
    assertThat(mapper.primaryClassToDto(null)).isEqualTo(tech.maze.dtos.assets.enums.PrimaryClass.UNRECOGNIZED);
    assertThat(mapper.primaryClassToDto(PrimaryClass.FIAT)).isEqualTo(tech.maze.dtos.assets.enums.PrimaryClass.FIAT);
    assertThat(mapper.primaryClassToDto(PrimaryClass.CRYPTO)).isEqualTo(tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO);
  }
}

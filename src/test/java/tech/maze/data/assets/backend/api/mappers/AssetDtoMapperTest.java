package tech.maze.data.assets.backend.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Value;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;

class AssetDtoMapperTest {
  private final PrimaryClassDtoMapper primaryClassDtoMapper = new PrimaryClassDtoMapper();
  private final DocumentValueDtoMapper documentValueDtoMapper = new DocumentValueDtoMapper(
      Mappers.getMapper(tech.maze.commons.mappers.UuidMapper.class)
  );

  @Test
  void uuidToValueReturnsDefaultForNull() {
    assertThat(documentValueDtoMapper.uuidToValue(null)).isEqualTo(Value.getDefaultInstance());
  }

  @Test
  void uuidToValueReturnsStringValueForUuid() {
    final java.util.UUID id = java.util.UUID.randomUUID();

    assertThat(documentValueDtoMapper.uuidToValue(id).getStringValue()).isEqualTo(id.toString());
  }

  @Test
  void primaryClassToDtoHandlesNullAndKnownValues() {
    assertThat(primaryClassDtoMapper.toDto(null)).isEqualTo(tech.maze.dtos.assets.enums.PrimaryClass.UNRECOGNIZED);
    assertThat(primaryClassDtoMapper.toDto(PrimaryClass.FIAT)).isEqualTo(tech.maze.dtos.assets.enums.PrimaryClass.FIAT);
    assertThat(primaryClassDtoMapper.toDto(PrimaryClass.CRYPTO)).isEqualTo(tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO);
  }
}

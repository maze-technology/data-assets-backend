package tech.maze.data.assets.backend.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
  void primaryClassToDtoHandlesKnownValuesAndRejectsNull() {
    assertThatThrownBy(() -> primaryClassDtoMapper.toDto(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("primaryClass must not be null");
    assertThat(primaryClassDtoMapper.toDto(PrimaryClass.FIAT)).isEqualTo(tech.maze.dtos.assets.enums.PrimaryClass.FIAT);
    assertThat(primaryClassDtoMapper.toDto(PrimaryClass.CRYPTO)).isEqualTo(tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO);
  }

  @Test
  void primaryClassToDomainHandlesKnownValuesAndRejectsInvalidInputs() {
    assertThat(primaryClassDtoMapper.toDomain(tech.maze.dtos.assets.enums.PrimaryClass.FIAT))
        .isEqualTo(PrimaryClass.FIAT);
    assertThat(primaryClassDtoMapper.toDomain(tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO))
        .isEqualTo(PrimaryClass.CRYPTO);
    assertThatThrownBy(() -> primaryClassDtoMapper.toDomain(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("primaryClass must not be null");
    assertThatThrownBy(() -> primaryClassDtoMapper.toDomain(tech.maze.dtos.assets.enums.PrimaryClass.UNRECOGNIZED))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("primaryClass must be defined");
  }
}

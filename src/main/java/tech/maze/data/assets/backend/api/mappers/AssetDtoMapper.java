package tech.maze.data.assets.backend.api.mappers;

import com.google.protobuf.Value;
import java.util.UUID;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import tech.maze.commons.mappers.BaseDtoMapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;

/**
 * Maps between asset domain and DTO models.
 */
@Mapper(
    componentModel = "spring",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {
      BaseDtoMapper.class
    }
)
public interface AssetDtoMapper {
  /**
   * Maps a domain asset to its DTO representation.
   */
  @Mapping(target = "id", source = "id", qualifiedByName = "uuidToValue")
  @Mapping(target = "primaryClass", source = "primaryClass", qualifiedByName = "primaryClassToDto")
  tech.maze.dtos.assets.models.Asset toDto(Asset asset);

  @Named("uuidToValue")
  default Value uuidToValue(UUID value) {
    if (value == null) {
      return Value.getDefaultInstance();
    }
    return Value.newBuilder().setStringValue(value.toString()).build();
  }

  @Named("primaryClassToDto")
  default tech.maze.dtos.assets.enums.PrimaryClass primaryClassToDto(PrimaryClass value) {
    if (value == null) {
      return tech.maze.dtos.assets.enums.PrimaryClass.UNRECOGNIZED;
    }

    return switch (value) {
      case FIAT -> tech.maze.dtos.assets.enums.PrimaryClass.FIAT;
      case CRYPTO -> tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO;
    };
  }
}

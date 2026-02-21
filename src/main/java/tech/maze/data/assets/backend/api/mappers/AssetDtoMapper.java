package tech.maze.data.assets.backend.api.mappers;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import tech.maze.commons.mappers.BaseDtoMapper;
import tech.maze.data.assets.backend.domain.models.Asset;

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
      BaseDtoMapper.class,
      DocumentValueDtoMapper.class,
      PrimaryClassDtoMapper.class
    }
)
public interface AssetDtoMapper {
  /**
   * Maps a domain asset to its DTO representation.
   */
  @Mapping(target = "id", source = "id", qualifiedByName = "uuidToValue")
  @Mapping(target = "primaryClass", source = "primaryClass", qualifiedByName = "primaryClassToDto")
  tech.maze.dtos.assets.models.Asset toDto(Asset asset);
}

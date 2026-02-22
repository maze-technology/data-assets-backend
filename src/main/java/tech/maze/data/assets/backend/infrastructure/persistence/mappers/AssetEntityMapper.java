package tech.maze.data.assets.backend.infrastructure.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.infrastructure.persistence.entities.AssetEntity;

/**
 * Maps asset entities to domain models and back.
 */
@Mapper(componentModel = "spring")
public interface AssetEntityMapper {
  /**
   * Generated method.
   */
  Asset toDomain(AssetEntity entity);

  /**
   * Generated method.
   */
  @Mapping(target = "dataProvidersMetaDatas", ignore = true)
  AssetEntity toEntity(Asset asset);
}

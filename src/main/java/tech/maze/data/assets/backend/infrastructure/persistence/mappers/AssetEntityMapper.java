package tech.maze.data.assets.backend.infrastructure.persistence.mappers;

import org.mapstruct.Mapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.infrastructure.persistence.entities.AssetEntity;

@Mapper(componentModel = "spring")
/**
 * Generated type.
 */
public interface AssetEntityMapper {
  /**
   * Generated method.
   */
  Asset toDomain(AssetEntity entity);
  /**
   * Generated method.
   */
  AssetEntity toEntity(Asset asset);
}

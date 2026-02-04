package tech.maze.data.assets.backend.infrastructure.persistence.mappers;

import org.mapstruct.Mapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.infrastructure.persistence.entities.AssetEntity;

@Mapper(componentModel = "spring")
public interface AssetEntityMapper {
  Asset toDomain(AssetEntity entity);
  AssetEntity toEntity(Asset asset);
}

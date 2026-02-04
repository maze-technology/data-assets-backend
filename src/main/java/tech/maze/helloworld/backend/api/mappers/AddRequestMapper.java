package tech.maze.helloworld.backend.api.mappers;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.maze.dtos.helloworld.payloads.AddRequest;
import tech.maze.helloworld.backend.domain.models.Message;

/**
 * Mapper for converting AddRequest DTOs to domain Message models.
 */
@Mapper(
    componentModel = "spring",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AddRequestMapper {
  AddRequestMapper INSTANCE = Mappers.getMapper(AddRequestMapper.class);

  /**
   * Maps an AddRequest DTO to a Domain Message model.
   *
   * @param request the AddRequest DTO
   * @return the corresponding Domain Message model
   */
  Message toDomain(AddRequest request);
}

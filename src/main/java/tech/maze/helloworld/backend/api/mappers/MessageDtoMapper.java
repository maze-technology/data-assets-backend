package tech.maze.helloworld.backend.api.mappers;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.maze.commons.mappers.BaseDtoMapper;
import tech.maze.commons.mappers.UuidMapper;
import tech.maze.helloworld.backend.domain.models.Message;

/**
 * Mapper for converting between Domain Message model and its corresponding DTO model.
 * This mapper handles the conversion between the domain layer and the API layer.
 */
@Mapper(
    componentModel = "spring",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {
      BaseDtoMapper.class,
      UuidMapper.class
    }
)
public interface MessageDtoMapper {
  public static final MessageDtoMapper INSTANCE = Mappers.getMapper(MessageDtoMapper.class);

  /**
   * Maps a Domain Message model to its corresponding DTO model.
   *
   * @param message the Domain Message model to convert
   * @return the corresponding Message DTO
   */
  tech.maze.dtos.helloworld.models.Message toDto(Message message);

  /**
   * Maps a Message DTO to a Domain Message model.
   *
   * @param messageDto the Message DTO to convert
   * @return the corresponding Domain Message model
   */
  Message toDomain(tech.maze.dtos.helloworld.models.Message messageDto);
}


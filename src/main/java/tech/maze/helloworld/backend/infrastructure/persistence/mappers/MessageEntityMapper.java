package tech.maze.helloworld.backend.infrastructure.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.infrastructure.persistence.entities.MessageEntity;

/**
 * Mapper for converting between Domain Message model and MessageEntity.
 * This mapper handles the conversion between the domain layer and the persistence layer.
 */
@Mapper(
    componentModel = "spring",
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MessageEntityMapper {
  /**
   * Maps a Domain Message model to a MessageEntity.
   *
   * @param message the Domain Message model to convert
   * @return the corresponding MessageEntity
   */
  MessageEntity toEntity(Message message);

  /**
   * Maps a MessageEntity to a Domain Message model.
   *
   * @param entity the MessageEntity to convert
   * @return the corresponding Domain Message model
   */
  Message toDomain(MessageEntity entity);
}


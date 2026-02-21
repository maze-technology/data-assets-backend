package tech.maze.data.assets.backend.api.mappers;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;

/**
 * Maps between domain and DTO primary class enums.
 */
@Component
public class PrimaryClassDtoMapper {
  /**
   * Converts domain primary class values to DTO enum values.
   */
  @Named("primaryClassToDto")
  public tech.maze.dtos.assets.enums.PrimaryClass toDto(PrimaryClass value) {
    if (value == null) {
      throw new IllegalArgumentException("primaryClass must not be null");
    }

    return switch (value) {
      case FIAT -> tech.maze.dtos.assets.enums.PrimaryClass.FIAT;
      case CRYPTO -> tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO;
    };
  }

  /**
   * Converts DTO primary class values to domain enum values.
   */
  public PrimaryClass toDomain(tech.maze.dtos.assets.enums.PrimaryClass value) {
    if (value == null) {
      throw new IllegalArgumentException("primaryClass must not be null");
    }

    return switch (value) {
      case FIAT -> PrimaryClass.FIAT;
      case CRYPTO -> PrimaryClass.CRYPTO;
      case UNRECOGNIZED -> throw new IllegalArgumentException(
          "primaryClass must be defined"
      );
      default -> throw new IllegalArgumentException(
          "primaryClass must be defined"
      );
    };
  }
}

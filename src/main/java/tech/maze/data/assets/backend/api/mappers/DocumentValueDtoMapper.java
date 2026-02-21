package tech.maze.data.assets.backend.api.mappers;

import com.google.protobuf.Value;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import tech.maze.commons.mappers.UuidMapper;

/**
 * Maps UUID values to protobuf document values.
 */
@Component
@RequiredArgsConstructor
public class DocumentValueDtoMapper {
  private final UuidMapper uuidMapper;

  /**
   * Converts UUID values to protobuf {@link Value} wrappers.
   */
  @Named("uuidToValue")
  public Value uuidToValue(UUID value) {
    String serialized = uuidMapper.map(value);
    if (serialized == null) {
      return Value.getDefaultInstance();
    }

    return Value.newBuilder().setStringValue(serialized).build();
  }
}

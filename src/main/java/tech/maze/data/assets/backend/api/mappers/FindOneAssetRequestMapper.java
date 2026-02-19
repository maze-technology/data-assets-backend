package tech.maze.data.assets.backend.api.mappers;

import com.google.protobuf.Message;
import com.google.protobuf.Value;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Maps FindOne requests into domain lookup keys.
 */
@Service
public class FindOneAssetRequestMapper {
  /**
   * Extracts an asset id from a FindOne request criterion if present.
   */
  public UUID toId(tech.maze.dtos.assets.requests.FindOneRequest request) {
    if (request == null) {
      return null;
    }

    final Object criterion = getField(request, "criterion");
    if (!(criterion instanceof Message criterionMessage)) {
      return null;
    }

    final Object filter = getField(criterionMessage, "filter");
    if (!(filter instanceof Message filterMessage)) {
      return null;
    }

    final Object byId = getField(filterMessage, "byId");
    if (byId instanceof Value value && value.hasStringValue()) {
      return parseUuid(value.getStringValue());
    }

    return null;
  }

  private static UUID parseUuid(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    try {
      return UUID.fromString(raw);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  private static Object getField(Message message, String fieldName) {
    final var field = message.getDescriptorForType().findFieldByName(fieldName);
    if (field == null) {
      return null;
    }
    return message.getField(field);
  }
}

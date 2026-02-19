package tech.maze.data.assets.backend.api.support;

import com.google.protobuf.Message;
import com.google.protobuf.Value;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Extracts UUID ids from requests carrying criterion.filter.byId fields.
 */
@Service
public class CriterionRequestIdExtractor {
  /**
   * Extracts an id from a protobuf request with criterion/filter/byId fields.
   */
  public UUID extractId(Message request) {
    if (request == null) {
      return null;
    }

    Object criterion = getField(request, "criterion");
    if (!(criterion instanceof Message criterionMessage)) {
      return null;
    }

    Object filter = getField(criterionMessage, "filter");
    if (!(filter instanceof Message filterMessage)) {
      return null;
    }

    Object byId = getField(filterMessage, "byId");
    if (byId instanceof Value value && value.hasStringValue()) {
      return parseUuid(value.getStringValue());
    }

    return null;
  }

  private static UUID parseUuid(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  private static Object getField(Message message, String fieldName) {
    com.google.protobuf.Descriptors.FieldDescriptor field =
        message.getDescriptorForType().findFieldByName(fieldName);
    if (field == null) {
      return null;
    }
    return message.getField(field);
  }
}

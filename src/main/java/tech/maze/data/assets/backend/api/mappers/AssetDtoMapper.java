package tech.maze.data.assets.backend.api.mappers;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;

/**
 * Maps between asset domain and DTO models.
 */
@Service
public class AssetDtoMapper {
  /**
   * Maps a domain asset to its DTO representation.
   */
  public tech.maze.dtos.assets.models.Asset toDto(Asset asset) {
    if (asset == null) {
      return tech.maze.dtos.assets.models.Asset.getDefaultInstance();
    }

    final tech.maze.dtos.assets.models.Asset.Builder builder =
        tech.maze.dtos.assets.models.Asset.newBuilder();

    setIfPresent(builder, "id", toDocument(asset.id()));
    setIfPresent(builder, "symbol", asset.symbol());
    setIfPresent(builder, "name", asset.name());
    if (asset.primaryClass() != null) {
      setEnumIfPresent(builder, "primaryClass", asset.primaryClass().name());
    }
    if (asset.createdAt() != null) {
      setIfPresent(builder, "createdAt", toTimestamp(asset.createdAt()));
    }

    return builder.build();
  }

  private static Value toDocument(UUID uuid) {
    if (uuid == null) {
      return Value.getDefaultInstance();
    }
    return Value.newBuilder().setStringValue(uuid.toString()).build();
  }

  private static Timestamp toTimestamp(Instant instant) {
    return Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano())
        .build();
  }

  private static void setIfPresent(Message.Builder builder, String fieldName, Object value) {
    final var field = builder.getDescriptorForType().findFieldByName(fieldName);
    if (field != null && value != null) {
      builder.setField(field, value);
    }
  }

  private static void setEnumIfPresent(Message.Builder builder, String fieldName, String enumName) {
    final var field = builder.getDescriptorForType().findFieldByName(fieldName);
    if (field == null || enumName == null || !field.getJavaType().equals(
        com.google.protobuf.Descriptors.FieldDescriptor.JavaType.ENUM)) {
      return;
    }

    final var value = field.getEnumType().findValueByName(enumName);
    if (value != null) {
      builder.setField(field, value);
    }
  }
}

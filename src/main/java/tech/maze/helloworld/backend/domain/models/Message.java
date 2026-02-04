package tech.maze.helloworld.backend.domain.models;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Domain model representing a Message.
 * This is a pure domain model without any infrastructure concerns (no JPA annotations).
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
  UUID id;
  String content;
  Instant createdAt;
}


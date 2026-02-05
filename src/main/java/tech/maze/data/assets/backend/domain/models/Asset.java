package tech.maze.data.assets.backend.domain.models;

import java.time.Instant;
import java.util.UUID;

/**
 * Generated type.
 */
public record Asset(
    UUID id,
    String symbol,
    String name,
    PrimaryClass primaryClass,
    Instant createdAt
) {}

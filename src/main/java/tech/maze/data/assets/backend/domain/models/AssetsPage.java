package tech.maze.data.assets.backend.domain.models;

import java.util.List;

/**
 * Paged assets query result.
 */
public record AssetsPage(
    List<Asset> assets,
    long totalElements,
    long totalPages
) {}

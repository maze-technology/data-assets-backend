package tech.maze.data.assets.backend.api.search;

import com.google.protobuf.Value;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;
import tech.maze.dtos.assets.search.Criterion;

/**
 * FindOne strategy that resolves assets by criterion.filter.byId.
 */
@Service
@RequiredArgsConstructor
public class ByIdFindOneAssetSearchStrategy implements FindOneAssetSearchStrategy {
  private final FindAssetUseCase findAssetUseCase;

  @Override
  public boolean supports(Criterion criterion) {
    return criterion != null
        && criterion.hasFilter()
        && criterion.getFilter().hasById()
        && criterion.getFilter().getById().hasStringValue();
  }

  @Override
  public Optional<Asset> search(Criterion criterion) {
    Value value = criterion.getFilter().getById();
    try {
      UUID id = UUID.fromString(value.getStringValue());
      return findAssetUseCase.findById(id);
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
  }
}

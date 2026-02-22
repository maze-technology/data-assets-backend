package tech.maze.data.assets.backend.api.search;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.commons.mappers.ProtobufValueMapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;
import tech.maze.dtos.assets.search.Criterion;

/**
 * FindOne strategy that resolves assets by criterion.filter.byId.
 */
@Service
@RequiredArgsConstructor
public class FindOneAssetByIdSearchStrategy implements FindOneAssetSearchStrategy {
  private final FindAssetUseCase findAssetUseCase;
  private final ProtobufValueMapper protobufValueMapper;

  @Override
  public boolean supports(Criterion criterion) {
    return criterion != null
        && criterion.hasFilter()
        && criterion.getFilter().hasById()
        && criterion.getFilter().getById().hasStringValue();
  }

  @Override
  public Optional<Asset> search(Criterion criterion) {
    return protobufValueMapper.toUuid(criterion.getFilter().getById())
        .flatMap(findAssetUseCase::findById);
  }
}

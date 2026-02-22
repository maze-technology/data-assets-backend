package tech.maze.data.assets.backend.api.search;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.api.mappers.PrimaryClassDtoMapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;
import tech.maze.dtos.assets.search.Criterion;
import tech.maze.dtos.assets.search.CriterionFilterBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass;

/**
 * FindOne strategy that resolves assets by symbol, name and primary class.
 */
@Service
@RequiredArgsConstructor
public class FindOneAssetBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClassSearchStrategy
    implements FindOneAssetSearchStrategy {
  private final FindAssetUseCase findAssetUseCase;
  private final PrimaryClassDtoMapper primaryClassDtoMapper;

  @Override
  public boolean supports(Criterion criterion) {
    if (criterion == null
        || !criterion.hasFilter()
        || !criterion.getFilter().hasBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass()) {
      return false;
    }

    CriterionFilterBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass filter =
        criterion.getFilter().getBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass();
    return !filter.getSymbol().isBlank()
        && !filter.getName().isBlank()
        && filter.getPrimaryClass() != tech.maze.dtos.assets.enums.PrimaryClass.UNRECOGNIZED;
  }

  @Override
  public Optional<Asset> search(Criterion criterion) {
    CriterionFilterBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass filter =
        criterion.getFilter().getBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass();
    final PrimaryClass primaryClass;
    try {
      primaryClass = primaryClassDtoMapper.toDomain(filter.getPrimaryClass());
    } catch (IllegalArgumentException ignored) {
      return Optional.empty();
    }

    return findAssetUseCase.findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
        filter.getSymbol(),
        filter.getName(),
        primaryClass
    );
  }
}

package tech.maze.data.assets.backend.api.search;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.api.support.CriterionValueExtractor;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;
import tech.maze.dtos.assets.search.Criterion;
import tech.maze.dtos.assets.search.CriterionFilterByDataProviderIdAndDataProviderMetaDatasAssetId;

/**
 * FindOne strategy that resolves assets by data provider and provider-side asset id.
 */
@Service
@RequiredArgsConstructor
public class FindOneAssetByDataProviderIdAndDataProviderMetaDatasAssetIdSearchStrategy
    implements FindOneAssetSearchStrategy {
  private final FindAssetUseCase findAssetUseCase;
  private final CriterionValueExtractor criterionValueExtractor;

  @Override
  public boolean supports(Criterion criterion) {
    if (criterion == null || !criterion.hasFilter()
        || !criterion.getFilter().hasByDataProviderIdAndDataProviderMetaDatasAssetId()) {
      return false;
    }

    CriterionFilterByDataProviderIdAndDataProviderMetaDatasAssetId filter =
        criterion.getFilter().getByDataProviderIdAndDataProviderMetaDatasAssetId();
    return filter.hasDataProviderId()
        && criterionValueExtractor.extractUuid(filter.getDataProviderId()).isPresent()
        && !filter.getDataProviderMetaDatasAssetId().isBlank();
  }

  @Override
  public Optional<Asset> search(Criterion criterion) {
    CriterionFilterByDataProviderIdAndDataProviderMetaDatasAssetId filter =
        criterion.getFilter().getByDataProviderIdAndDataProviderMetaDatasAssetId();
    Optional<UUID> dataProviderId = criterionValueExtractor.extractUuid(filter.getDataProviderId());
    if (dataProviderId.isEmpty()) {
      return Optional.empty();
    }

    return findAssetUseCase.findByDataProviderIdAndDataProviderMetaDatasAssetId(
        dataProviderId.get(),
        filter.getDataProviderMetaDatasAssetId()
    );
  }
}

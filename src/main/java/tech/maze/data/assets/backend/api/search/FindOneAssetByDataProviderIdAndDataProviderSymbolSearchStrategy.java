package tech.maze.data.assets.backend.api.search;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.maze.commons.mappers.ProtobufValueMapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;
import tech.maze.dtos.assets.search.Criterion;
import tech.maze.dtos.assets.search.CriterionFilterByDataProviderIdAndDataProviderSymbol;

/**
 * FindOne strategy that resolves assets by data provider and provider-side symbol.
 */
@Service
@RequiredArgsConstructor
public class FindOneAssetByDataProviderIdAndDataProviderSymbolSearchStrategy
    implements FindOneAssetSearchStrategy {
  private final FindAssetUseCase findAssetUseCase;
  private final ProtobufValueMapper protobufValueMapper;

  @Override
  public boolean supports(Criterion criterion) {
    if (criterion == null
        || !criterion.hasFilter()
        || !criterion.getFilter().hasByDataProviderIdAndDataProviderSymbol()) {
      return false;
    }

    CriterionFilterByDataProviderIdAndDataProviderSymbol filter =
        criterion.getFilter().getByDataProviderIdAndDataProviderSymbol();
    return filter.hasDataProviderId()
        && protobufValueMapper.toUuid(filter.getDataProviderId()).isPresent()
        && !filter.getSymbol().isBlank();
  }

  @Override
  public Optional<Asset> search(Criterion criterion) {
    CriterionFilterByDataProviderIdAndDataProviderSymbol filter =
        criterion.getFilter().getByDataProviderIdAndDataProviderSymbol();
    Optional<UUID> dataProviderId = protobufValueMapper.toUuid(filter.getDataProviderId());
    if (dataProviderId.isEmpty()) {
      return Optional.empty();
    }

    return findAssetUseCase.findByDataProviderIdAndDataProviderSymbol(
        dataProviderId.get(),
        filter.getSymbol()
    );
  }
}

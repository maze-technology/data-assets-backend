package tech.maze.data.assets.backend.api.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.protobuf.Value;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import tech.maze.commons.mappers.ProtobufValueMapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;

@ExtendWith(MockitoExtension.class)
class FindOneAssetByDataProviderIdAndDataProviderMetaDatasAssetIdSearchStrategyTest {
  private static final ProtobufValueMapper PROTOBUF_VALUE_MAPPER =
      Mappers.getMapper(ProtobufValueMapper.class);

  @Mock
  private FindAssetUseCase findAssetUseCase;
  @Mock
  private Asset asset;

  @Test
  void supportsOnlyWhenDataProviderIdAndAssetIdArePresent() {
    final var strategy = new FindOneAssetByDataProviderIdAndDataProviderMetaDatasAssetIdSearchStrategy(
        findAssetUseCase,
        PROTOBUF_VALUE_MAPPER
    );
    final UUID dataProviderId = UUID.randomUUID();

    assertThat(strategy.supports(criterion(dataProviderId.toString(), "BTC"))).isTrue();
    assertThat(strategy.supports(criterion("", "BTC"))).isFalse();
    assertThat(strategy.supports(criterion(dataProviderId.toString(), " "))).isFalse();
  }

  @Test
  void searchDelegatesToFindUseCase() {
    final var strategy = new FindOneAssetByDataProviderIdAndDataProviderMetaDatasAssetIdSearchStrategy(
        findAssetUseCase,
        PROTOBUF_VALUE_MAPPER
    );
    final UUID dataProviderId = UUID.randomUUID();
    final var criterion = criterion(dataProviderId.toString(), "BTC");
    when(findAssetUseCase.findByDataProviderIdAndDataProviderMetaDatasAssetId(dataProviderId, "BTC"))
        .thenReturn(Optional.of(asset));

    final var result = strategy.search(criterion);

    assertThat(result).contains(asset);
    verify(findAssetUseCase).findByDataProviderIdAndDataProviderMetaDatasAssetId(dataProviderId, "BTC");
  }

  @Test
  void searchReturnsEmptyWhenDataProviderIdIsInvalid() {
    final var strategy = new FindOneAssetByDataProviderIdAndDataProviderMetaDatasAssetIdSearchStrategy(
        findAssetUseCase,
        PROTOBUF_VALUE_MAPPER
    );

    final var result = strategy.search(criterion("not-a-uuid", "BTC"));

    assertThat(result).isEmpty();
    verifyNoInteractions(findAssetUseCase);
  }

  private static tech.maze.dtos.assets.search.Criterion criterion(String dataProviderId, String assetId) {
    return tech.maze.dtos.assets.search.Criterion.newBuilder()
        .setFilter(
            tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                .setByDataProviderIdAndDataProviderMetaDatasAssetId(
                    tech.maze.dtos.assets.search.CriterionFilterByDataProviderIdAndDataProviderMetaDatasAssetId
                        .newBuilder()
                        .setDataProviderId(Value.newBuilder().setStringValue(dataProviderId).build())
                        .setDataProviderMetaDatasAssetId(assetId)
                        .build()
                )
                .build()
        )
        .build();
  }
}

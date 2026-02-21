package tech.maze.data.assets.backend.api.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.api.mappers.PrimaryClassDtoMapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;

@ExtendWith(MockitoExtension.class)
class FindOneAssetBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClassSearchStrategyTest {
  @Mock
  private FindAssetUseCase findAssetUseCase;
  @Mock
  private Asset asset;

  @Test
  void supportsOnlyWhenAllCriterionFieldsAreUsable() {
    final var strategy = new FindOneAssetBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClassSearchStrategy(
        findAssetUseCase,
        new PrimaryClassDtoMapper()
    );

    assertThat(strategy.supports(criterion("btc", "bitcoin", tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO))).isTrue();
    assertThat(strategy.supports(criterion(" ", "bitcoin", tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO))).isFalse();
    assertThat(strategy.supports(criterion("btc", " ", tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO))).isFalse();
    assertThat(strategy.supports(criterionWithUnknownPrimaryClass("btc", "bitcoin"))).isFalse();
  }

  @Test
  void searchDelegatesToFindUseCase() {
    final var strategy = new FindOneAssetBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClassSearchStrategy(
        findAssetUseCase,
        new PrimaryClassDtoMapper()
    );
    final var criterion = criterion("btc", "bitcoin", tech.maze.dtos.assets.enums.PrimaryClass.CRYPTO);
    when(findAssetUseCase.findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass("btc", "bitcoin", PrimaryClass.CRYPTO))
        .thenReturn(Optional.of(asset));

    final var result = strategy.search(criterion);

    assertThat(result).contains(asset);
    verify(findAssetUseCase).findBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass("btc", "bitcoin", PrimaryClass.CRYPTO);
  }

  @Test
  void searchReturnsEmptyWhenPrimaryClassCannotBeMapped() {
    final var strategy = new FindOneAssetBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClassSearchStrategy(
        findAssetUseCase,
        new PrimaryClassDtoMapper()
    );
    final var criterion = criterionWithUnknownPrimaryClass("btc", "bitcoin");

    final var result = strategy.search(criterion);

    assertThat(result).isEmpty();
    verifyNoInteractions(findAssetUseCase);
  }

  private static tech.maze.dtos.assets.search.Criterion criterion(
      String symbol,
      String name,
      tech.maze.dtos.assets.enums.PrimaryClass primaryClass
  ) {
    return tech.maze.dtos.assets.search.Criterion.newBuilder()
        .setFilter(
            tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                .setBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
                    tech.maze.dtos.assets.search.CriterionFilterBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass
                        .newBuilder()
                        .setSymbol(symbol)
                        .setName(name)
                        .setPrimaryClass(primaryClass)
                        .build()
                )
                .build()
        )
        .build();
  }

  private static tech.maze.dtos.assets.search.Criterion criterionWithUnknownPrimaryClass(String symbol, String name) {
    return tech.maze.dtos.assets.search.Criterion.newBuilder()
        .setFilter(
            tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                .setBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass(
                    tech.maze.dtos.assets.search.CriterionFilterBySymbolIgnoreCaseAndNameIgnoreCaseAndPrimaryClass
                        .newBuilder()
                        .setSymbol(symbol)
                        .setName(name)
                        .setPrimaryClassValue(999)
                        .build()
                )
                .build()
        )
        .build();
  }
}

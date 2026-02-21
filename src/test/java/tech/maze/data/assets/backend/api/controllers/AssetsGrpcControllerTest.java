package tech.maze.data.assets.backend.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.protobuf.Value;
import io.grpc.stub.StreamObserver;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maze.data.assets.backend.api.mappers.AssetDtoMapper;
import tech.maze.data.assets.backend.api.search.FindOneAssetSearchStrategyHandler;
import tech.maze.data.assets.backend.api.support.CriterionValueExtractor;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.AssetsPage;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;
import tech.maze.data.assets.backend.domain.ports.in.BlacklistAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.in.SearchAssetsUseCase;
import tech.maze.data.assets.backend.domain.ports.in.WhitelistAssetUseCase;

@ExtendWith(MockitoExtension.class)
class AssetsGrpcControllerTest {
  @Mock
  private FindOneAssetSearchStrategyHandler findOneAssetSearchStrategyHandler;
  @Mock
  private SearchAssetsUseCase searchAssetsUseCase;
  @Mock
  private CriterionValueExtractor criterionValueExtractor;
  @Mock
  private AssetDtoMapper assetDtoMapper;
  @Mock
  private BlacklistAssetUseCase blacklistAssetUseCase;
  @Mock
  private WhitelistAssetUseCase whitelistAssetUseCase;

  @Mock
  private StreamObserver<tech.maze.dtos.assets.requests.FindOneResponse> findOneObserver;
  @Mock
  private StreamObserver<tech.maze.dtos.assets.requests.FindByDataProvidersResponse> findByProvidersObserver;
  @Mock
  private StreamObserver<tech.maze.dtos.assets.requests.BlacklistResponse> blacklistObserver;
  @Mock
  private StreamObserver<tech.maze.dtos.assets.requests.WhitelistResponse> whitelistObserver;

  @Test
  void findOneSetsAssetWhenCriterionIsPresentAndMatchFound() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionValueExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final UUID id = UUID.randomUUID();
    final var request = tech.maze.dtos.assets.requests.FindOneRequest.newBuilder()
        .setCriterion(
            tech.maze.dtos.assets.search.Criterion.newBuilder()
                .setFilter(
                    tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                        .setById(Value.newBuilder().setStringValue(id.toString()).build())
                        .build()
                )
                .build()
        )
        .build();
    final var asset = new Asset(id, "BTC", "Bitcoin", PrimaryClass.CRYPTO, Instant.now(), false);
    final var dto = tech.maze.dtos.assets.models.Asset.newBuilder().setSymbol("BTC").build();

    when(findOneAssetSearchStrategyHandler.handleSearch(request.getCriterion())).thenReturn(Optional.of(asset));
    when(assetDtoMapper.toDto(asset)).thenReturn(dto);

    controller.findOne(request, findOneObserver);

    final ArgumentCaptor<tech.maze.dtos.assets.requests.FindOneResponse> captor =
        ArgumentCaptor.forClass(tech.maze.dtos.assets.requests.FindOneResponse.class);
    verify(findOneObserver).onNext(captor.capture());
    verify(findOneObserver).onCompleted();
    assertThat(captor.getValue().hasAsset()).isTrue();
    assertThat(captor.getValue().getAsset()).isEqualTo(dto);
  }

  @Test
  void findOneReturnsEmptyResponseWhenCriterionMissing() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionValueExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final var request = tech.maze.dtos.assets.requests.FindOneRequest.newBuilder().build();

    controller.findOne(request, findOneObserver);

    final ArgumentCaptor<tech.maze.dtos.assets.requests.FindOneResponse> captor =
        ArgumentCaptor.forClass(tech.maze.dtos.assets.requests.FindOneResponse.class);
    verify(findOneObserver).onNext(captor.capture());
    verify(findOneObserver).onCompleted();
    verifyNoInteractions(findOneAssetSearchStrategyHandler, assetDtoMapper);
    assertThat(captor.getValue().hasAsset()).isFalse();
  }

  @Test
  void findByDataProvidersReturnsMappedAssets() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionValueExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final var assetA = new Asset(UUID.randomUUID(), "BTC", "Bitcoin", PrimaryClass.CRYPTO, Instant.now(), false);
    final var assetB = new Asset(UUID.randomUUID(), "EUR", "Euro", PrimaryClass.FIAT, Instant.now(), false);
    final var dtoA = tech.maze.dtos.assets.models.Asset.newBuilder().setSymbol("BTC").build();
    final var dtoB = tech.maze.dtos.assets.models.Asset.newBuilder().setSymbol("EUR").build();

    final UUID dataProviderA = UUID.randomUUID();
    final UUID dataProviderB = UUID.randomUUID();
    final var request = tech.maze.dtos.assets.requests.FindByDataProvidersRequest.newBuilder()
        .addDataProviders(Value.newBuilder().setStringValue(dataProviderA.toString()).build())
        .addDataProviders(Value.newBuilder().setStringValue(dataProviderB.toString()).build())
        .setPagination(
            tech.maze.dtos.assets.search.Pagination.newBuilder()
                .setPage(0)
                .setLimit(50)
                .build()
        )
        .build();
    when(criterionValueExtractor.extractUuids(request.getDataProvidersList())).thenReturn(List.of(dataProviderA, dataProviderB));
    when(searchAssetsUseCase.findByDataProviderIds(List.of(dataProviderA, dataProviderB), 0, 50))
        .thenReturn(new AssetsPage(List.of(assetA, assetB), 2, 1));
    when(assetDtoMapper.toDto(assetA)).thenReturn(dtoA);
    when(assetDtoMapper.toDto(assetB)).thenReturn(dtoB);

    controller.findByDataProviders(request, findByProvidersObserver);

    final ArgumentCaptor<tech.maze.dtos.assets.requests.FindByDataProvidersResponse> captor =
        ArgumentCaptor.forClass(tech.maze.dtos.assets.requests.FindByDataProvidersResponse.class);
    verify(findByProvidersObserver).onNext(captor.capture());
    verify(findByProvidersObserver).onCompleted();
    verify(criterionValueExtractor).extractUuids(request.getDataProvidersList());
    verify(searchAssetsUseCase).findByDataProviderIds(List.of(dataProviderA, dataProviderB), 0, 50);
    assertThat(captor.getValue().getAssetsList()).containsExactly(dtoA, dtoB);
    assertThat(captor.getValue().getPaginationInfos().getTotalElements()).isEqualTo(2);
    assertThat(captor.getValue().getPaginationInfos().getTotalPages()).isEqualTo(1);
  }

  @Test
  void blacklistDelegatesToUseCaseWhenCriterionResolvesAsset() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionValueExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final UUID id = UUID.randomUUID();
    final var criterion = tech.maze.dtos.assets.search.Criterion.newBuilder()
        .setFilter(
            tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                .setById(Value.newBuilder().setStringValue(id.toString()).build())
                .build()
        )
        .build();
    final var request = tech.maze.dtos.assets.requests.BlacklistRequest.newBuilder()
        .setCriterion(criterion)
        .build();
    when(findOneAssetSearchStrategyHandler.handleSearch(criterion))
        .thenReturn(Optional.of(new Asset(id, "BTC", "Bitcoin", PrimaryClass.CRYPTO, Instant.now(), false)));

    controller.blacklist(request, blacklistObserver);

    verify(findOneAssetSearchStrategyHandler).handleSearch(criterion);
    verify(blacklistAssetUseCase).blacklist(id);
    verify(blacklistObserver).onNext(tech.maze.dtos.assets.requests.BlacklistResponse.getDefaultInstance());
    verify(blacklistObserver).onCompleted();
  }

  @Test
  void blacklistReturnsErrorWhenAssetAlreadyBlacklisted() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionValueExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final UUID id = UUID.randomUUID();
    final var criterion = tech.maze.dtos.assets.search.Criterion.newBuilder()
        .setFilter(
            tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                .setById(Value.newBuilder().setStringValue(id.toString()).build())
                .build()
        )
        .build();
    final var request = tech.maze.dtos.assets.requests.BlacklistRequest.newBuilder()
        .setCriterion(criterion)
        .build();
    when(findOneAssetSearchStrategyHandler.handleSearch(criterion))
        .thenReturn(Optional.of(new Asset(id, "BTC", "Bitcoin", PrimaryClass.CRYPTO, Instant.now(), true)));

    controller.blacklist(request, blacklistObserver);

    verify(blacklistObserver).onError(org.mockito.ArgumentMatchers.any(Throwable.class));
    verifyNoInteractions(blacklistAssetUseCase);
  }

  @Test
  void whitelistDelegatesToUseCaseWhenCriterionResolvesAsset() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionValueExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final UUID id = UUID.randomUUID();
    final var criterion = tech.maze.dtos.assets.search.Criterion.newBuilder()
        .setFilter(
            tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                .setById(Value.newBuilder().setStringValue(id.toString()).build())
                .build()
        )
        .build();
    final var request = tech.maze.dtos.assets.requests.WhitelistRequest.newBuilder()
        .setCriterion(criterion)
        .build();
    when(findOneAssetSearchStrategyHandler.handleSearch(criterion))
        .thenReturn(Optional.of(new Asset(id, "BTC", "Bitcoin", PrimaryClass.CRYPTO, Instant.now(), true)));

    controller.whitelist(request, whitelistObserver);

    verify(findOneAssetSearchStrategyHandler).handleSearch(criterion);
    verify(whitelistAssetUseCase).whitelist(id);
    verify(whitelistObserver).onNext(tech.maze.dtos.assets.requests.WhitelistResponse.getDefaultInstance());
    verify(whitelistObserver).onCompleted();
  }

  @Test
  void whitelistReturnsErrorWhenAssetAlreadyWhitelisted() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionValueExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final UUID id = UUID.randomUUID();
    final var criterion = tech.maze.dtos.assets.search.Criterion.newBuilder()
        .setFilter(
            tech.maze.dtos.assets.search.CriterionFilter.newBuilder()
                .setById(Value.newBuilder().setStringValue(id.toString()).build())
                .build()
        )
        .build();
    final var request = tech.maze.dtos.assets.requests.WhitelistRequest.newBuilder()
        .setCriterion(criterion)
        .build();
    when(findOneAssetSearchStrategyHandler.handleSearch(criterion))
        .thenReturn(Optional.of(new Asset(id, "BTC", "Bitcoin", PrimaryClass.CRYPTO, Instant.now(), false)));

    controller.whitelist(request, whitelistObserver);

    verify(whitelistObserver).onError(org.mockito.ArgumentMatchers.any(Throwable.class));
    verifyNoInteractions(whitelistAssetUseCase);
  }
}

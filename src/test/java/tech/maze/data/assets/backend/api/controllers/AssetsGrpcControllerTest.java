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
import tech.maze.data.assets.backend.api.support.CriterionRequestIdExtractor;
import tech.maze.data.assets.backend.domain.models.Asset;
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
  private CriterionRequestIdExtractor criterionRequestIdExtractor;
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
        criterionRequestIdExtractor,
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
        criterionRequestIdExtractor,
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
        criterionRequestIdExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final var assetA = new Asset(UUID.randomUUID(), "BTC", "Bitcoin", PrimaryClass.CRYPTO, Instant.now(), false);
    final var assetB = new Asset(UUID.randomUUID(), "EUR", "Euro", PrimaryClass.FIAT, Instant.now(), false);
    final var dtoA = tech.maze.dtos.assets.models.Asset.newBuilder().setSymbol("BTC").build();
    final var dtoB = tech.maze.dtos.assets.models.Asset.newBuilder().setSymbol("EUR").build();

    when(searchAssetsUseCase.findAll()).thenReturn(List.of(assetA, assetB));
    when(assetDtoMapper.toDto(assetA)).thenReturn(dtoA);
    when(assetDtoMapper.toDto(assetB)).thenReturn(dtoB);

    controller.findByDataProviders(
        tech.maze.dtos.assets.requests.FindByDataProvidersRequest.newBuilder().build(),
        findByProvidersObserver
    );

    final ArgumentCaptor<tech.maze.dtos.assets.requests.FindByDataProvidersResponse> captor =
        ArgumentCaptor.forClass(tech.maze.dtos.assets.requests.FindByDataProvidersResponse.class);
    verify(findByProvidersObserver).onNext(captor.capture());
    verify(findByProvidersObserver).onCompleted();
    assertThat(captor.getValue().getAssetsList()).containsExactly(dtoA, dtoB);
  }

  @Test
  void blacklistDelegatesToUseCase() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionRequestIdExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final UUID id = UUID.randomUUID();
    final var request = tech.maze.dtos.assets.requests.BlacklistRequest.newBuilder().build();
    when(criterionRequestIdExtractor.extractId(request)).thenReturn(id);

    controller.blacklist(request, blacklistObserver);

    verify(criterionRequestIdExtractor).extractId(request);
    verify(blacklistAssetUseCase).blacklist(id);
    verify(blacklistObserver).onNext(tech.maze.dtos.assets.requests.BlacklistResponse.getDefaultInstance());
    verify(blacklistObserver).onCompleted();
  }

  @Test
  void whitelistDelegatesToUseCase() {
    final var controller = new AssetsGrpcController(
        findOneAssetSearchStrategyHandler,
        searchAssetsUseCase,
        criterionRequestIdExtractor,
        assetDtoMapper,
        blacklistAssetUseCase,
        whitelistAssetUseCase
    );
    final UUID id = UUID.randomUUID();
    final var request = tech.maze.dtos.assets.requests.WhitelistRequest.newBuilder().build();
    when(criterionRequestIdExtractor.extractId(request)).thenReturn(id);

    controller.whitelist(request, whitelistObserver);

    verify(criterionRequestIdExtractor).extractId(request);
    verify(whitelistAssetUseCase).whitelist(id);
    verify(whitelistObserver).onNext(tech.maze.dtos.assets.requests.WhitelistResponse.getDefaultInstance());
    verify(whitelistObserver).onCompleted();
  }
}

package tech.maze.data.assets.backend.api.controllers;

import io.grpc.stub.StreamObserver;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.api.mappers.AssetDtoMapper;
import tech.maze.data.assets.backend.api.search.FindOneAssetSearchStrategyHandler;
import tech.maze.data.assets.backend.api.support.CriterionRequestIdExtractor;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.BlacklistAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.in.SearchAssetsUseCase;
import tech.maze.data.assets.backend.domain.ports.in.WhitelistAssetUseCase;

/**
 * gRPC controller for assets API operations.
 */
@RequiredArgsConstructor
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AssetsGrpcController
    extends tech.maze.dtos.assets.controllers.AssetsGRPCGrpc.AssetsGRPCImplBase {
  FindOneAssetSearchStrategyHandler findOneAssetSearchStrategyHandler;
  SearchAssetsUseCase searchAssetsUseCase;
  CriterionRequestIdExtractor criterionRequestIdExtractor;
  AssetDtoMapper assetDtoMapper;
  BlacklistAssetUseCase blacklistAssetUseCase;
  WhitelistAssetUseCase whitelistAssetUseCase;

  @Override
  public void findOne(
      tech.maze.dtos.assets.requests.FindOneRequest request,
      StreamObserver<tech.maze.dtos.assets.requests.FindOneResponse> responseObserver
  ) {
    tech.maze.dtos.assets.requests.FindOneResponse.Builder responseBuilder =
        tech.maze.dtos.assets.requests.FindOneResponse.newBuilder();
    if (request.hasCriterion()) {
      findOneAssetSearchStrategyHandler.handleSearch(request.getCriterion())
          .map(assetDtoMapper::toDto)
          .ifPresent(responseBuilder::setAsset);
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void findByDataProviders(
      tech.maze.dtos.assets.requests.FindByDataProvidersRequest request,
      StreamObserver<tech.maze.dtos.assets.requests.FindByDataProvidersResponse> responseObserver
  ) {
    final List<Asset> assets = searchAssetsUseCase.findAll();
    tech.maze.dtos.assets.requests.FindByDataProvidersResponse response =
        tech.maze.dtos.assets.requests.FindByDataProvidersResponse.newBuilder()
            .addAllAssets(assets.stream().map(assetDtoMapper::toDto).toList())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void blacklist(
      tech.maze.dtos.assets.requests.BlacklistRequest request,
      StreamObserver<tech.maze.dtos.assets.requests.BlacklistResponse> responseObserver
  ) {
    java.util.UUID id = criterionRequestIdExtractor.extractId(request);
    blacklistAssetUseCase.blacklist(id);

    tech.maze.dtos.assets.requests.BlacklistResponse response =
        tech.maze.dtos.assets.requests.BlacklistResponse.newBuilder().build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void whitelist(
      tech.maze.dtos.assets.requests.WhitelistRequest request,
      StreamObserver<tech.maze.dtos.assets.requests.WhitelistResponse> responseObserver
  ) {
    java.util.UUID id = criterionRequestIdExtractor.extractId(request);
    whitelistAssetUseCase.whitelist(id);

    tech.maze.dtos.assets.requests.WhitelistResponse response =
        tech.maze.dtos.assets.requests.WhitelistResponse.newBuilder().build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}

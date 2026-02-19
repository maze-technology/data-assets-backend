package tech.maze.data.assets.backend.api.controllers;

import io.grpc.stub.StreamObserver;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tech.maze.data.assets.backend.api.mappers.AssetDtoMapper;
import tech.maze.data.assets.backend.api.mappers.FindOneAssetRequestMapper;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.ports.in.FindAssetUseCase;
import tech.maze.data.assets.backend.domain.ports.in.SearchAssetsUseCase;

/**
 * gRPC controller for assets API operations.
 */
@RequiredArgsConstructor
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AssetsGrpcController
    extends tech.maze.dtos.assets.controllers.AssetsGRPCGrpc.AssetsGRPCImplBase {
  FindAssetUseCase findAssetUseCase;
  SearchAssetsUseCase searchAssetsUseCase;
  FindOneAssetRequestMapper findOneAssetRequestMapper;
  AssetDtoMapper assetDtoMapper;

  @Override
  public void findOne(
      tech.maze.dtos.assets.requests.FindOneRequest request,
      StreamObserver<tech.maze.dtos.assets.requests.FindOneResponse> responseObserver
  ) {
    final var responseBuilder = tech.maze.dtos.assets.requests.FindOneResponse.newBuilder();
    final var id = findOneAssetRequestMapper.toId(request);

    if (id != null) {
      findAssetUseCase.findById(id)
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
    final var response = tech.maze.dtos.assets.requests.FindByDataProvidersResponse.newBuilder()
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
    responseObserver.onNext(tech.maze.dtos.assets.requests.BlacklistResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void whitelist(
      tech.maze.dtos.assets.requests.WhitelistRequest request,
      StreamObserver<tech.maze.dtos.assets.requests.WhitelistResponse> responseObserver
  ) {
    responseObserver.onNext(tech.maze.dtos.assets.requests.WhitelistResponse.newBuilder().build());
    responseObserver.onCompleted();
  }
}

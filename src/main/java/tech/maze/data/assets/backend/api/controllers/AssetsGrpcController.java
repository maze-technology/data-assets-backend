package tech.maze.data.assets.backend.api.controllers;

import io.grpc.stub.StreamObserver;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tech.maze.commons.exceptions.GrpcStatusException;
import tech.maze.commons.pagination.Pagination;
import tech.maze.commons.pagination.PaginationUtils;
import tech.maze.data.assets.backend.api.mappers.AssetDtoMapper;
import tech.maze.data.assets.backend.api.search.FindOneAssetSearchStrategyHandler;
import tech.maze.data.assets.backend.api.support.CriterionValueExtractor;
import tech.maze.data.assets.backend.domain.models.Asset;
import tech.maze.data.assets.backend.domain.models.AssetsPage;
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
  CriterionValueExtractor criterionValueExtractor;
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
    final List<java.util.UUID> dataProviderIds =
        criterionValueExtractor.extractUuids(request.getDataProvidersList());
    final Pagination pagination = PaginationUtils.normalize(
        request.hasPagination() ? request.getPagination().getPage() : 0L,
        request.hasPagination() ? request.getPagination().getLimit() : 50L,
        50
    );
    final AssetsPage assetsPage = searchAssetsUseCase.findByDataProviderIds(
        dataProviderIds,
        pagination.page(),
        pagination.limit()
    );
    final List<Asset> assets = assetsPage.assets();

    tech.maze.dtos.assets.requests.FindByDataProvidersResponse response =
        tech.maze.dtos.assets.requests.FindByDataProvidersResponse.newBuilder()
            .addAllAssets(assets.stream().map(assetDtoMapper::toDto).toList())
            .setPaginationInfos(
                tech.maze.dtos.assets.search.PaginationInfos.newBuilder()
                    .setTotalElements(assetsPage.totalElements())
                    .setTotalPages(assetsPage.totalPages())
                    .build()
            )
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void blacklist(
      tech.maze.dtos.assets.requests.BlacklistRequest request,
      StreamObserver<tech.maze.dtos.assets.requests.BlacklistResponse> responseObserver
  ) {
    if (!request.hasCriterion()) {
      throw GrpcStatusException.invalidArgument("criterion is required");
    }

    final Asset asset = findOneAssetSearchStrategyHandler.handleSearch(request.getCriterion())
        .orElse(null);
    if (asset == null) {
      throw GrpcStatusException.notFound("Asset not found");
    }

    blacklistAssetUseCase.blacklist(asset.id());

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
    if (!request.hasCriterion()) {
      throw GrpcStatusException.invalidArgument("criterion is required");
    }

    final Asset asset = findOneAssetSearchStrategyHandler.handleSearch(request.getCriterion())
        .orElse(null);
    if (asset == null) {
      throw GrpcStatusException.notFound("Asset not found");
    }

    whitelistAssetUseCase.whitelist(asset.id());

    tech.maze.dtos.assets.requests.WhitelistResponse response =
        tech.maze.dtos.assets.requests.WhitelistResponse.newBuilder().build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}

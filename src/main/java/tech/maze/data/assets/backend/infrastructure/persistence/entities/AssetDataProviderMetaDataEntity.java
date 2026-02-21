package tech.maze.data.assets.backend.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;

/**
 * JPA entity for asset data-provider metadata.
 */
@Entity
@Table(name = "assets_dataproviders_metadatas")
@Getter
@Setter
@NoArgsConstructor
public class AssetDataProviderMetaDataEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "asset_id", nullable = false)
  private AssetEntity asset;

  @Column(name = "data_provider_id", nullable = false)
  private UUID dataProviderId;

  @Column(name = "provider_asset_id", nullable = false)
  private String providerAssetId;

  @Column(nullable = false)
  private String symbol;

  @Column(nullable = false)
  private String name;

  @Column(name = "primary_class", nullable = false)
  private PrimaryClass primaryClass;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "extra_datas")
  private Map<String, String> extraDatas;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "tool_box")
  private Map<String, String> toolBox;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}

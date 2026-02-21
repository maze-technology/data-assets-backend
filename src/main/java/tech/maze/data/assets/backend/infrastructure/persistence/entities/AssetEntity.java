package tech.maze.data.assets.backend.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;

/**
 * JPA entity for assets.
 */
@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
public class AssetEntity {
  @Id
  private UUID id;

  @Column(nullable = false)
  private String symbol;

  @Column(nullable = false)
  private String name;

  @Column(name = "primary_class", nullable = false)
  private PrimaryClass primaryClass;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "blacklisted", nullable = false)
  private Boolean blacklisted;

  @OneToMany(mappedBy = "asset")
  private List<AssetDataProviderMetaDataEntity> dataProvidersMetaDatas;
}

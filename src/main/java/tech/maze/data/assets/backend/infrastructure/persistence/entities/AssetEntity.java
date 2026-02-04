package tech.maze.data.assets.backend.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.maze.data.assets.backend.domain.models.PrimaryClass;

@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
/**
 * Generated type.
 */
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
}

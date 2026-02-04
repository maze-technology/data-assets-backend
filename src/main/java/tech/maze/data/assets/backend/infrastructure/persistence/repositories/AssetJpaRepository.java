package tech.maze.data.assets.backend.infrastructure.persistence.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.maze.data.assets.backend.infrastructure.persistence.entities.AssetEntity;

@Repository
public interface AssetJpaRepository extends JpaRepository<AssetEntity, UUID> {}

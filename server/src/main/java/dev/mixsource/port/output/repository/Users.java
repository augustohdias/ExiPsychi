package dev.mixsource.port.output.repository;

import dev.mixsource.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Users extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(final String username);

    Boolean existsByUsername(final String username);
}

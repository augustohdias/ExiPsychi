package dev.mixsource.port.output.repository;

import dev.mixsource.model.entity.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface Characters extends JpaRepository<CharacterEntity, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE CharacterEntity c SET c.x = :x, c.y = :y WHERE c.id = :id")
    void updateXAndYById(@Param("id") Long id, @Param("x") Double x, @Param("y") Double y);


    List<CharacterEntity> findAllByUserId(final Long userId);

}

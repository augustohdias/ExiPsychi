package dev.mixsource.model.entity;

import dev.mixsource.model.Action;
import dev.mixsource.model.CharacterModel;
import dev.mixsource.model.Direction;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "characters", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double x;

    private double y;

    private long level;

    private long currentLevelKillCount;

    private long totalKillCount;

    private int powerPoints;

    private int healthPoints;

    private int soulPoints;

    private int dodgePoints;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public CharacterModel toCharacter() {
        return new CharacterModel(
                id,
                x,
                y,
                name,
                level,
                currentLevelKillCount,
                totalKillCount,
                powerPoints,
                healthPoints,
                soulPoints,
                dodgePoints,
                user.getId(),
                Action.IDLE,
                Direction.DOWN,
                0
            );
    }

    public static CharacterEntity fromCharacter(CharacterModel character) {
        final UserEntity user = new UserEntity();
        user.setId(character.getUserId());
        return new CharacterEntity(
                character.getId(),
                character.getX(),
                character.getY(),
                character.getLevel(),
                character.getCurrentLevelKillCount(),
                character.getTotalKillCount(),
                character.getPowerPoints(),
                character.getHealthPoints(),
                character.getSoulPoints(),
                character.getDodgePoints(),
                character.getName(),
                user
        );
    }
}

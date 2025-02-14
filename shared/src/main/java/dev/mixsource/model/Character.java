package dev.mixsource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Character {
    private Long id;

    private Double x;

    private Double y;

    private String name;

    // ------------- maybe another class

    private Long level;

    private Long currentLevelKillCount;

    private Long totalKillCount;

    // ------------- end

    // ------------- maybe another class

    private Integer powerPoints;

    private Integer healthPoints;

    private Integer soulPoints;

    private Integer dodgePoints;

    // ------------- end

    private Long userId;

    private Action action;

    private Direction direction = Direction.DOWN;

    private Integer lastProcessedSequence = 0;

    public Character(final Long id, final Double x, final Double y, final Action action, final String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
        this.action = action;
    }
}

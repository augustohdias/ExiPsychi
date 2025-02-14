package dev.mixsource.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Command {
    private CommandType commandType;
    private Direction direction;
    private CastType mainCastType;
    private CastType secondaryCastType;
    private int x;
    private int y;
    private long targetId;
    private LocalDateTime atTime;

    public enum CommandType {
        MOVE,
        DASH,
        ATTACK,
        CAST,
        INTERACT,
        STOP,
        ;
    }
}

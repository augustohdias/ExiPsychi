package dev.mixsource.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    private static final Command STOP = new Command(CommandType.STOP, null, null, null, 0, 0, 0, null);
    private static final Command MOVE = new Command(CommandType.MOVE, null, null, null, 0, 0, 0, null);

    public static Command stop() {
        final Command command = STOP.clone();
        command.setAtTime(LocalDateTime.now());
        return command;
    }

    public static Command move(final Direction direction) {
        final Command command = MOVE.clone();
        command.setDirection(direction);
        command.setAtTime(LocalDateTime.now());
        return command;
    }

    
    @Override
    public Command clone() {
        final Command command = new Command();
        command.setCommandType(this.commandType);
        command.setDirection(this.direction);
        command.setMainCastType(this.mainCastType);
        command.setSecondaryCastType(this.secondaryCastType);
        command.setX(this.x);
        command.setY(this.y);
        command.setTargetId(this.targetId);
        command.setAtTime(this.atTime);
        return command;
    }

    @Override
    public String toString() {
        return commandType.name();
    }
}

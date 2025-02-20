package dev.mixsource.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.model.Command;
import dev.mixsource.model.Direction;
import dev.mixsource.model.UserInput;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.badlogic.gdx.utils.Timer;

public class GameInputListener extends InputListener {
    private final Map<Integer, Boolean> keyIsPressed;
    private final WebSocketServerConnector connector;
    private final ObjectMapper mapper;
    private final Queue<Command> pendingCommands = new ConcurrentLinkedQueue<>();

    private final Timer.Task sendTask = new Timer.Task() {
        @Override
        public void run() {
            sendPendingCommands();
        }
    };
    private Direction currentDirection = null;

    public GameInputListener(final WebSocketServerConnector connector, final ObjectMapper mapper) {
        super();
        this.keyIsPressed = new ConcurrentHashMap<>();
        this.connector = connector;
        this.mapper = mapper;
        keyIsPressed.put(Input.Keys.W, false);
        keyIsPressed.put(Input.Keys.S, false);
        keyIsPressed.put(Input.Keys.A, false);
        keyIsPressed.put(Input.Keys.D, false);
        new Timer().scheduleTask(sendTask, 0, 30 / 1000f); // 30ms
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        handleKeyPress(keycode, true);
        return true;
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        handleKeyPress(keycode, false);
        return true;
    }

    private Direction getDirectionFromKeycode(int keycode) {
        return switch (keycode) {
            case Input.Keys.W -> Direction.UP;
            case Input.Keys.S -> Direction.DOWN;
            case Input.Keys.A -> Direction.LEFT;
            case Input.Keys.D -> Direction.RIGHT;
            default -> null;
        };
    }
    
    private void handleKeyPress(int keycode, boolean isPressed) {
        Direction direction = getDirectionFromKeycode(keycode);
        if (direction != null) {
            keyIsPressed.put(keycode, isPressed);
            updateCurrentDirection();
        }
    }

    private void updateCurrentDirection() {
        Direction newDirection = null;
        if (keyIsPressed.get(Input.Keys.W)) newDirection = Direction.UP;
        if (keyIsPressed.get(Input.Keys.S)) newDirection = Direction.DOWN;
        if (keyIsPressed.get(Input.Keys.A)) newDirection = Direction.LEFT;
        if (keyIsPressed.get(Input.Keys.D)) newDirection = Direction.RIGHT;
        
        if (newDirection != currentDirection) {
            currentDirection = newDirection;
            if (currentDirection != null) {
                pendingCommands.add(Command.move(currentDirection));
            } else {
                pendingCommands.add(Command.stop());
            }
        }
    }

    private void sendPendingCommands() {
        List<Command> commands = new ArrayList<>();
        while (!pendingCommands.isEmpty()) {
            commands.add(pendingCommands.poll());
        }
        
        if (!commands.isEmpty()) {
            try {
                UserInput input = new UserInput();
                input.setIssuedCommands(commands);
                input.setClientTime(LocalDateTime.now());
                connector.sendMessage(mapper.writeValueAsString(input));
            } catch (JsonProcessingException e) {
                Gdx.app.error("Network", "Error sending commands: " + e.getMessage());
            }
        }
    }
}

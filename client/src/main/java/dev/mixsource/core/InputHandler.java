package dev.mixsource.core;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.TimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.model.Command;
import dev.mixsource.model.Command.CommandType;
import dev.mixsource.model.Direction;
import dev.mixsource.model.Character;
import com.badlogic.gdx.Gdx;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputHandler {
    private final Character selectedCharacter;
    private final WebSocketServerConnector serverConnector;
    private final ObjectMapper mapper;
    
    private final Map<Integer, Boolean> keyState = new HashMap<>();
    private int lastSequence = 0;
    private long lastSendTime = 0;
    private final Map<Integer, PendingInput> pendingInputs = new HashMap<>();
    private boolean wasMoving = false;

    public InputHandler(Character character, WebSocketServerConnector connector, ObjectMapper mapper) {
        this.selectedCharacter = character;
        this.serverConnector = connector;
        this.mapper = mapper;
        
        keyState.put(Input.Keys.W, false);
        keyState.put(Input.Keys.A, false);
        keyState.put(Input.Keys.S, false);
        keyState.put(Input.Keys.D, false);
    }

    public void update(float deltaTime) {
        checkMovementInputs(deltaTime);
    }

    private void checkMovementInputs(float deltaTime) {
        updateMovement(deltaTime);
        
        boolean isMoving = Gdx.input.isKeyPressed(Input.Keys.W) || 
                          Gdx.input.isKeyPressed(Input.Keys.S) || 
                          Gdx.input.isKeyPressed(Input.Keys.A) || 
                          Gdx.input.isKeyPressed(Input.Keys.D);
        
        if (wasMoving && !isMoving) {
            sendStopCommand();
        }
        wasMoving = isMoving;
    }

    private void updateMovement(float deltaTime) {
        keyState.keySet().forEach(key -> {
            boolean isPressed = Gdx.input.isKeyPressed(key);
            if (keyState.get(key) != isPressed) {
                keyState.put(key, isPressed);
                if (isPressed) {
                    Command command = getCommandFromKey(key);
                    sendInputToServer(command);
                }
            }
        });
    }

    private Command getCommandFromKey(int key) {
        CommandType type = isMoving() ? CommandType.MOVE : CommandType.STOP;
        Direction direction = getCurrentDirection();
        
        return new Command(
            type,
            direction,
            null,
            null,
            0,
            0,
            0,
            LocalDateTime.now()
        );
    }

    private void sendInputToServer(Command command) {
        long currentTime = TimeUtils.millis();
        if (currentTime - lastSendTime > 50) {
            dev.mixsource.model.Input input = new dev.mixsource.model.Input();
            input.setIssuedCommands(List.of(command));
            input.setClientTime(LocalDateTime.now());
            input.setSequenceNumber(++lastSequence);
            
            pendingInputs.put(lastSequence, new PendingInput(
                selectedCharacter.getX(),
                selectedCharacter.getY(),
                System.currentTimeMillis()
            ));
            
            try {
                serverConnector.sendMessage(mapper.writeValueAsString(input));
                lastSendTime = currentTime;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendStopCommand() {
        Command stopCommand = new Command(
            CommandType.STOP, 
            null, 
            null, 
            null, 
            0,
            0,
            0, 
            LocalDateTime.now()
        );
        sendInputToServer(stopCommand);
    }

    public void reconcile(Character serverCharacter) {
        selectedCharacter.setX(serverCharacter.getX());
        selectedCharacter.setY(serverCharacter.getY());
        
        pendingInputs.entrySet().removeIf(entry -> {
            if (entry.getKey() <= serverCharacter.getLastProcessedSequence()) {
                return true;
            }
            applyPendingInput(entry.getValue());
            return false;
        });
    }

    private void applyPendingInput(PendingInput input) {
        // Usar velocidade fixa do servidor (0.5 unidades por 30ms)
        double serverSpeed = 0.5 * (System.currentTimeMillis() - input.timestamp) / 30.0;
        
        double newX = input.x + (selectedCharacter.getX() - input.x) * serverSpeed;
        double newY = input.y + (selectedCharacter.getY() - input.y) * serverSpeed;
        
        selectedCharacter.setX(newX);
        selectedCharacter.setY(newY);
    }

    private boolean isMoving() {
        return Gdx.input.isKeyPressed(Input.Keys.W) ||
               Gdx.input.isKeyPressed(Input.Keys.S) ||
               Gdx.input.isKeyPressed(Input.Keys.A) ||
               Gdx.input.isKeyPressed(Input.Keys.D);
    }

    private Direction getCurrentDirection() {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) return Direction.UP;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) return Direction.DOWN;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) return Direction.LEFT;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) return Direction.RIGHT;
        return Direction.DOWN;
    }

    private static class PendingInput {
        final double x;
        final double y;
        final long timestamp;

        PendingInput(double x, double y, long timestamp) {
            this.x = x;
            this.y = y;
            this.timestamp = timestamp;
        }
    }
} 
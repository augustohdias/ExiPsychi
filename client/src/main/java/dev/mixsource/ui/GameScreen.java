package dev.mixsource.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mixsource.model.Character;
import com.badlogic.gdx.Input;
import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.model.Command;
import com.badlogic.gdx.utils.TimeUtils;
import dev.mixsource.model.Direction;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

public class GameScreen {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Character selectedCharacter; // Personagem escolhido pelo usuário
    private List<Character> characters; // Lista dos personagens a renderizar
    private ObjectMapper mapper;
    private Map<Integer, Boolean> keyState;
    private WebSocketServerConnector serverConnector;
    private long lastSendTime = 0;
    private int lastSequence = 0;
    private boolean wasMoving = false;

    public GameScreen(Character selectedCharacter, WebSocketServerConnector serverConnector) {
        this.selectedCharacter = selectedCharacter;
        this.serverConnector = serverConnector;
        this.characters = new ArrayList<>();
        this.characters.add(selectedCharacter);
        
        // Configurar câmera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        shapeRenderer = new ShapeRenderer();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // Inicializar estados das teclas
        keyState = new HashMap<>();
        keyState.put(Input.Keys.W, false);
        keyState.put(Input.Keys.A, false);
        keyState.put(Input.Keys.S, false);
        keyState.put(Input.Keys.D, false);
    }

    /**
     * Atualiza a lista de personagens a partir da mensagem JSON recebida do
     * servidor.
     * Supondo que a mensagem seja um array de objetos Character.
     */
    public void updateCharacters(String message) {
        try {
            Character[] updatedCharacters = mapper.readValue(message, Character[].class);
            List<Character> newCharacters = new ArrayList<>();
            
            for (Character serverChar : updatedCharacters) {
                if (serverChar.getId().equals(selectedCharacter.getId())) {
                    // Suavizar a transição com interpolação
                    double alpha = 0.2;
                    double newX = selectedCharacter.getX() + (serverChar.getX() - selectedCharacter.getX()) * alpha;
                    double newY = selectedCharacter.getY() + (serverChar.getY() - selectedCharacter.getY()) * alpha;
                    selectedCharacter.setX(newX);
                    selectedCharacter.setY(newY);
                } else {
                    // Atualizar outros personagens diretamente
                    newCharacters.add(serverChar);
                }
            }
            this.characters = newCharacters;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Renderiza a tela do jogo desenhando cada personagem.
     * O personagem selecionado é desenhado em verde; os demais em vermelho.
     */
    public void render() {
        handleInput();
        
        // Atualizar posição da câmera para seguir o personagem
        camera.position.set(
            (float) (selectedCharacter.getX() * 10 + 5), 
            (float) (selectedCharacter.getY() * 10 + 5), 
            0
        );
        camera.update();
        
        // Limpar tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Iniciar renderização
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Renderizar personagem selecionado (verde)
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(
            (float) (selectedCharacter.getX() * 10), 
            (float) (selectedCharacter.getY() * 10), 
            10, 
            10
        );
        
        // Renderizar outros personagens (vermelho)
        for (Character c : characters) {
            if (!c.getId().equals(selectedCharacter.getId())) {
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.rect(
                    (float) (c.getX() * 10), 
                    (float) (c.getY() * 10), 
                    10, 
                    10
                );
            }
        }
        
        shapeRenderer.end();
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        boolean anyKeyPressed = false;
        Direction currentDirection = null;

        // Atualizar estados das teclas
        keyState.replaceAll((k, v) -> Gdx.input.isKeyPressed(k));
        
        // Processar movimento
        if (keyState.get(Input.Keys.W)) {
            selectedCharacter.setY(selectedCharacter.getY() + 5 * delta);
            anyKeyPressed = true;
            currentDirection = Direction.UP;
        }
        if (keyState.get(Input.Keys.S)) {
            selectedCharacter.setY(selectedCharacter.getY() - 5 * delta);
            anyKeyPressed = true;
            currentDirection = Direction.DOWN;
        }
        if (keyState.get(Input.Keys.A)) {
            selectedCharacter.setX(selectedCharacter.getX() - 5 * delta);
            anyKeyPressed = true;
            currentDirection = Direction.LEFT;
        }
        if (keyState.get(Input.Keys.D)) {
            selectedCharacter.setX(selectedCharacter.getX() + 5 * delta);
            anyKeyPressed = true;
            currentDirection = Direction.RIGHT;
        }

        // Enviar comandos
        if (anyKeyPressed) {
            sendInputToServer(Command.CommandType.MOVE, currentDirection);
        } else if (wasMoving) { // Enviar STOP apenas se estava movendo e parou
            sendInputToServer(Command.CommandType.STOP, null);
        }
        wasMoving = anyKeyPressed;
    }

    private void sendInputToServer(Command.CommandType type, Direction direction) {
        long currentTime = TimeUtils.millis();
        if (currentTime - lastSendTime > 50 || type == Command.CommandType.STOP) {
            try {
                Command command = new Command(
                    type,
                    direction,
                    null, 
                    null,
                    0,
                    0,
                    0,
                    LocalDateTime.now()
                );
                
                dev.mixsource.model.Input input = new dev.mixsource.model.Input();
                input.setIssuedCommands(List.of(command));
                input.setSequenceNumber(++lastSequence);
                
                String json = mapper.writeValueAsString(input);
                serverConnector.sendMessage(json);
                lastSendTime = currentTime;
                
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
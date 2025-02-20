package dev.mixsource.screen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import dev.mixsource.actors.PlayableActor;
import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.configuration.DefaultSkin;
import dev.mixsource.listeners.GameInputListener;
import dev.mixsource.model.CharacterModel;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

@Getter
@Setter
public class GameScreen implements Screen {

    private final Game gameClient;
    private final ObjectMapper mapper;
    private final Queue<CharacterModel> networkQueue;
    private final ArrayMap<Long, PlayableActor> actors = new ArrayMap<>();
    private final Stage stage;
    private final Group characterLayer;

    private WebSocketServerConnector connector;
    private CharacterModel playerCharacter;
    private PlayableActor selectedCharacter;
    private OrthographicCamera gameCamera;
    private Vector2 cameraTarget = new Vector2();
    private float cameraLerp = 0.1f;
    private FitViewport gameViewport;
    
    public GameScreen(final Game gameClient, final ObjectMapper mapper) {
        this.gameClient = gameClient;
        this.mapper = mapper;
        this.networkQueue = new ConcurrentLinkedQueue<>();
        this.gameCamera = new OrthographicCamera();
        this.gameViewport = new FitViewport(1280, 720);
        stage = new Stage(gameViewport);
        gameCamera = (OrthographicCamera) gameViewport.getCamera();

        characterLayer = new Group();

        stage.addActor(characterLayer);
    }

    private void processNetworkUpdates() {
        while (!networkQueue.isEmpty()) {
            final CharacterModel character = networkQueue.poll();
            updateOrCreateActor(character);
        }
    }
    
    private void updateOrCreateActor(CharacterModel character) {
        PlayableActor actor = actors.get(character.getId());
        if (actor == null) {
            actor = createNewActor(character);
            actors.put(character.getId(), actor);
            stage.addActor(actor);

            actor.setZIndex(0);
            if (character.getId().equals(this.playerCharacter.getId())) {
                selectedCharacter = actor;
                final int index = characterLayer.getChildren().size + 1;
                actor.setZIndex(index);
                Gdx.app.log("CAMERA", "Personagem principal definido: " + actor.getName());
            }
            Gdx.app.log("CAMERA", "Index: " + actor.getZIndex());
        } else {
            actor.syncWithModel(character);
        }
    }

    private PlayableActor createNewActor(final CharacterModel character) {
        // Selecionar textura baseado no ID do personagem
        TextureRegion texture = character.getId().equals(this.playerCharacter.getId()) 
                ? DefaultSkin.playerTexture() 
                : DefaultSkin.otherTexture();
        
        // Criar uma nova fonte específica para este ator
        BitmapFont nameFont = DefaultSkin.createNameFont();
        
        PlayableActor actor = new PlayableActor(character, nameFont, texture);
        
        // Posicionar corretamente no grid
        float gridX = character.getX().floatValue() * PlayableActor.CELL_SIZE;
        float gridY = character.getY().floatValue() * PlayableActor.CELL_SIZE;
        
        actor.setPosition(
            gridX - actor.getWidth()/2,
            gridY - actor.getHeight()/2
        );
        
        return actor;
    }

    private void handleServerUpdate(final String message) {
        try {
            Collection<CharacterModel> updatedCharacters = mapper.readValue(
                message, new TypeReference<Collection<CharacterModel>>() {}
            );
            
            // Processar atualizações
            updatedCharacters.forEach(networkQueue::add);
            
            // Obter IDs existentes de forma segura
            List<Long> existingIds = new ArrayList<>();
            for (int i = 0; i < actors.size; i++) {
                existingIds.add(actors.getKeyAt(i));
            }
            
            // Filtrar IDs para remoção
            List<Long> idsToRemove = existingIds.stream()
                .filter(id -> updatedCharacters.stream()
                    .noneMatch(c -> c.getId().equals(id)))
                .collect(Collectors.toList());
            
            // Remover atores não presentes na atualização
            idsToRemove.forEach(id -> {
                PlayableActor removed = actors.removeKey(id);
                if (removed != null) {
                    removed.dispose();
                    removed.remove();
                }
            });

        } catch (JsonProcessingException e) {
            Gdx.app.error(this.getClass().getName(), "Erro ao processar atualização: " + e.getMessage());
        }
    }
    
    @Override
    public void show() {
        stage.addListener(new GameInputListener(connector, mapper));
        Gdx.input.setInputProcessor(stage);
        
        connector.setOnUpdate(this::handleServerUpdate);
        connector.sendMessage("CONNECT_CHARACTER " + playerCharacter.getId());
        
        // Garantir que o personagem principal será criado via network update
        networkQueue.add(playerCharacter); // Adiciona o próprio personagem na fila
    }

    @Override
    public void render(float delta) {
        processNetworkUpdates();
        
        // Atualizar física antes da câmera
        for (PlayableActor actor : actors.values()) {
            actor.act(delta);
        }
        
        updateCamera(delta); // Mover para antes do clear
        
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    private void updateCamera(float delta) {
        if (selectedCharacter != null) {
            // Converter coordenadas do grid para pixels
            float centerX = selectedCharacter.getX() + selectedCharacter.getWidth()/2;
            float centerY = selectedCharacter.getY() + selectedCharacter.getHeight()/2;
            
            // Interpolação suave da câmera
            Vector3 cameraPosition = gameCamera.position;
            cameraPosition.x += (centerX - cameraPosition.x) * cameraLerp;
            cameraPosition.y += (centerY - cameraPosition.y) * cameraLerp;
            
            gameCamera.update();
            stage.getViewport().apply();
        }
    }

    @Override
    public void resize(final int width, final int height) {
        gameViewport.update(width, height, true);
        if(selectedCharacter != null) {
            // Usar posição atual do ator
            gameCamera.position.set(
                selectedCharacter.getX() + selectedCharacter.getWidth()/2,
                selectedCharacter.getY() + selectedCharacter.getHeight()/2,
                0f
            );
            gameCamera.update();
        }
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        
    }
}
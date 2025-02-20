package dev.mixsource.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import dev.mixsource.adapters.HttpAuthService;
import dev.mixsource.application.AuthService;
import dev.mixsource.configuration.DefaultSkin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.screen.CharacterSelectionScreen;
import dev.mixsource.screen.GameScreen;
import dev.mixsource.screen.LoginScreen;
import lombok.Getter;

@Getter
public class GameClient extends Game {
    private LoginScreen loginScreen;
    private CharacterSelectionScreen selectionScreen;
    private GameScreen gameScreen;

    private AuthService authService;
    private WebSocketServerConnector serverConnector;

    private boolean loggedIn = false;

    @Override
    public void create() {
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        authService = new HttpAuthService("http://localhost:8080/login");
        loginScreen = new LoginScreen(this, DefaultSkin.defaultLogin(), authService, mapper);
        selectionScreen = new CharacterSelectionScreen(this, DefaultSkin.defaultCharacterSelection());
        gameScreen = new GameScreen(this, mapper);
        this.setScreen(loginScreen);
/*
        authService = new HttpAuthService("http://localhost:8080/login");
        loginScreen = ScreenFactory.createLoginScreen(authService, (loginResponse) -> {
            Gdx.app.postRunnable(() -> {
                System.out.println("User Token: " + loginResponse.getUserToken());
                try {
                    System.out.println("Characters: " + new ObjectMapper().writeValueAsString(loginResponse.getCharacters()));
                } catch (Exception ignored) {}

                serverConnector = ConnectorFactory.createGameConnector(loginResponse);
                serverConnector.connect();

                this.selectionScreen = ScreenFactory.createCharacterSelectionScreen(
                        loginResponse,
                        (character) -> {
                            System.out.println("Personagem selecionado: " + character.getName());
                            serverConnector.sendMessage("CONNECT_CHARACTER " + character.getId());
                            this.gameScreen = ScreenFactory.createGameScreen(character, serverConnector);
                            this.selectionScreen = null;
                            Gdx.input.setInputProcessor(null);
                            serverConnector.setOnUpdate((message) -> {
                                if (gameScreen != null) {
                                    gameScreen.updateCharacters(message);
                                }
                            });
                        }
                );
                Gdx.input.setInputProcessor(this.selectionScreen.getStage());
                loggedIn = true;
            });
        });
*/
    }

    @Override
    public void dispose() {
        Gdx.app.log(this.getClass().getName(), "Desalocando GameClient");   
        try {
            if (loginScreen != null) {
                loginScreen.dispose();
            }
            if (selectionScreen != null) {
                selectionScreen.dispose();
            }
            if (gameScreen != null) {
                gameScreen.dispose();
            }
        } catch (Exception e) {
            Gdx.app.log(this.getClass().getName(), "Erro ao desalocar GameClient: " + e.getMessage());
        }
    }
}

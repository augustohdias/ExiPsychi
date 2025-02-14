package dev.mixsource;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import dev.mixsource.adapters.HttpAuthService;
import dev.mixsource.application.AuthService;
import dev.mixsource.ui.LoginScreen;
import com.badlogic.gdx.graphics.GL20;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.mixsource.ui.CharacterSelectionScreen;
import dev.mixsource.ui.GameScreen;
import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.factories.ScreenFactory;
import dev.mixsource.factories.ConnectorFactory;

public class GameClient extends ApplicationAdapter {

    private LoginScreen loginScreen;
    private CharacterSelectionScreen selectionScreen;
    private GameScreen gameScreen;
    private boolean loggedIn = false;
    private AuthService authService;
    private WebSocketServerConnector serverConnector;

    @Override
    public void create() {
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
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!loggedIn) {
            loginScreen.render(Gdx.graphics.getDeltaTime());
        } else if (gameScreen != null) {
            gameScreen.render();
        } else if (selectionScreen != null) {
            selectionScreen.render(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        if (loginScreen != null) {
            loginScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MMO Client");
        config.setWindowedMode(1280, 720);
        new Lwjgl3Application(new GameClient(), config);
    }
}

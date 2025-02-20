package dev.mixsource.screen;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import dev.mixsource.application.AuthService;
import dev.mixsource.core.GameClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mixsource.model.response.LoginResponse;

public class LoginScreen implements Screen {
    private final AuthService authService;
    private final Stage stage;
    private final Game game;
    private final Skin skin;
    private final ObjectMapper mapper;

    private TextField usernameField;
    private TextField passwordField;
    private TextButton loginButton;

    public LoginScreen(final Game game, final Skin skin, final AuthService authService, final ObjectMapper mapper) {
        this.game = game;
        this.skin = skin;
        this.authService = authService;
        this.mapper = mapper;

        this.stage = new Stage(new FitViewport(1280, 720));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.center();

        Container<Table> container = new Container<>(table);
        container.setBackground(createPanelBackground());
        container.pad(20);

        container.pack();
        container.setPosition(
                (Gdx.graphics.getWidth() - container.getPrefWidth()) / 2,
                (Gdx.graphics.getHeight() - container.getPrefHeight()) / 2);

        stage.addActor(container);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        loginButton = new TextButton("Login", skin);
        loginButton.pad(5);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    final String response = authService.authenticate(usernameField.getText(),
                            passwordField.getText());
                    final LoginResponse loginResponse = mapper.readValue(response, LoginResponse.class);
                    Gdx.app.log(this.getClass().getName(), "Login efetuado com resposta: " + loginResponse.getCharacters());
                    handleLogin(loginResponse);
                } catch (Exception e) {
                    Gdx.app.log(this.getClass().getName(), "Login falhou: " + e.getMessage());
                }
            }
        });

        table.add(usernameField).width(200).padBottom(10).row();
        table.add(passwordField).width(200).padBottom(10).row();
        table.add(loginButton).width(200).row();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

    private Drawable createPanelBackground() {
        Pixmap pixmap = new Pixmap(200, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.03f, 0.03f, 0.03f, 1f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        Drawable drawable = new TextureRegionDrawable(texture);
        pixmap.dispose();
        return drawable;
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    public void dispose() {
        Gdx.app.log(this.getClass().getName(), "Desalocando LoginScreen");
        try {
            stage.dispose();
            skin.dispose();
        } catch (Exception e) {
            Gdx.app.log(this.getClass().getName(), "Erro ao desalocar LoginScreen: " + e.getMessage());
        }
    }

    private void handleLogin(final LoginResponse loginResponse) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!username.isEmpty() && !password.isEmpty()) {
            Gdx.app.log(this.getClass().getName(), "Invocando tela de seleção de personagem. " + game.getClass().getName());
            ((GameClient) game).getSelectionScreen().setCharacters(loginResponse.getCharacters());
            ((GameClient) game).getSelectionScreen().setUserToken(loginResponse.getUserToken());
            game.setScreen(((GameClient) game).getSelectionScreen());
        }
    }
}
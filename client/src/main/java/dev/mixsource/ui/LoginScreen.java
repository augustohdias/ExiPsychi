package dev.mixsource.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.function.Consumer;

import dev.mixsource.application.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mixsource.model.response.LoginResponse;

public class LoginScreen {

    private Stage stage;
    private Skin skin;
    private TextField usernameField;
    private TextField passwordField;
    private TextButton loginButton;
    private AuthService authService;
    private boolean loginSuccessful = false;

    private Consumer<LoginResponse> onLoginSuccess;

    public LoginScreen(AuthService authService, Consumer<LoginResponse> onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage.addActor(new AnimatedBackground());
        initSkin();
        createUI();
    }

    private void initSkin() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin();
        skin.add("default-font", font);

        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        skin.add("default", textFieldStyle);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 1));
        pixmap.fill();
        Drawable blackDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        
        pixmap.setColor(new Color(0.8f, 0.8f, 0.8f, 1));
        pixmap.fill();
        Drawable lightGreyDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        
        pixmap.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
        pixmap.fill();
        Drawable darkGreyDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        

        pixmap.dispose();

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = blackDrawable;
        buttonStyle.down = darkGreyDrawable;
        buttonStyle.over = lightGreyDrawable;
        buttonStyle.pressedOffsetX = 0;
        buttonStyle.pressedOffsetY = 0;
        skin.add("default", buttonStyle);

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);
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

    private void createUI() {
        Table table = new Table();
        table.center();

        Container<Table> container = new Container<>(table);
        container.setBackground(createPanelBackground());
        container.pad(20);

        container.pack();
        container.setPosition(
            (Gdx.graphics.getWidth() - container.getPrefWidth()) / 2,
            (Gdx.graphics.getHeight() - container.getPrefHeight()) / 2
        );

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
                new Thread(() -> {
                    try {
                        String response = authService.authenticate(usernameField.getText(), passwordField.getText());
                        ObjectMapper mapper = new ObjectMapper();
                        LoginResponse loginResponse = mapper.readValue(response, LoginResponse.class);
                        System.out.println("Login efetuado com resposta: " + loginResponse);
                        loginSuccessful = true;
                        if (onLoginSuccess != null) {
                            onLoginSuccess.accept(loginResponse);
                        }
                    } catch (Exception e) {
                        System.out.println("Falha no login: " + e.getMessage());
                    }
                }).start();
            }
        });


        table.add(usernameField).width(200).padBottom(10).row();
        table.add(passwordField).width(200).padBottom(10).row();
        table.add(loginButton).width(200).row();
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
} 
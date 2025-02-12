package dev.mixsource;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameClient extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private WebSocketClient client;
    private SpriteBatch batch;
    private BitmapFont font;
    private Stage stage;
    private Skin skin;
    private TextField usernameField;
    private TextField passwordField;
    private TextButton loginButton;

    private static final int CELL_SIZE = 10;
    private int playerX = 0, playerY = 0;
    private String playerName;
    private boolean loggedIn = false;
    private final Map<String, int[]> otherPlayers = new HashMap<>();

    private void loadSkin() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin();
        skin.add("default-font", font);

        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
        skin.add("default", textFieldStyle);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        skin.add("default", buttonStyle);

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        loadSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");
        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        loginButton = new TextButton("Login", skin);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                authenticate(usernameField.getText(), passwordField.getText());
                System.out.println("Login pressed");
            }});

        table.add(usernameField).width(200).row();
        table.add(passwordField).width(200).row();
        table.add(loginButton).width(200).row();
    }

    private void authenticate(String username, String password) {
        HttpClient httpClient = HttpClient.newHttpClient();
        String jsonPayload = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println(response.body());
                });
    }

    private void setupGame() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(playerX * CELL_SIZE, playerY * CELL_SIZE, 0);
        camera.update();
        connectToServer();
    }

    private void connectToServer() {
        try {
            client = new WebSocketClient(new URI("ws://localhost:8080/game")) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to server.");
                }

                @Override
                public void onMessage(String message) {
                    String[] parts = message.split(" ");
                    if (parts[0].equals("UPDATE")) {
                        updateOtherPlayers(Arrays.copyOfRange(parts, 1, parts.length));
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {

                }

                @Override
                public void onError(Exception e) {

                }
            };
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOtherPlayers(String[] positions) {
        otherPlayers.clear();
        for (String pos : positions) {
            String[] xy = pos.split(",");
            if (xy.length == 2) {
                try {
                    int x = Integer.parseInt(xy[0]);
                    int y = Integer.parseInt(xy[1]);
                    if (!(x == playerX && y == playerY)) {
                        otherPlayers.put(pos, new int[]{x, y});
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        if (!loggedIn) {
            stage.act();
            stage.draw();
            return;
        }
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(playerX * CELL_SIZE, playerY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        shapeRenderer.setColor(1, 0, 0, 1);
        for (int[] pos : otherPlayers.values()) {
            shapeRenderer.rect(pos[0] * CELL_SIZE, pos[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        shapeRenderer.end();
    }

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("target/natives").getAbsolutePath());
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MMO Client");
        config.setWindowedMode(1280, 720);
        new Lwjgl3Application(new GameClient(), config);
    }
}
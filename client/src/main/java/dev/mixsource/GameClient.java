package dev.mixsource;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;

public class GameClient extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private WebSocketClient client;

    private static final int CELL_SIZE = 10;
    private int playerX = 0, playerY = 0;
    private final Map<String, int[]> otherPlayers = new HashMap<>();

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
                    if (parts[0].equals("START")) {
                        String[] pos = parts[1].split(",");
                        playerX = Integer.parseInt(pos[0]);
                        playerY = Integer.parseInt(pos[1]);
                        System.out.println("Spawned at: " + playerX + "," + playerY);
                    } else if (parts[0].equals("UPDATE")) {
                        updateOtherPlayers(Arrays.copyOfRange(parts, 1, parts.length));
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
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
        handleInput();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(playerX * CELL_SIZE, playerY * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        shapeRenderer.setColor(1, 0, 0, 1);
        for (int[] pos : otherPlayers.values()) {
            shapeRenderer.rect(pos[0] * CELL_SIZE, pos[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        shapeRenderer.end();
    }

    private void handleInput() {
        boolean moved = false;
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) { playerY++; moved = true; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) { playerY--; moved = true; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) { playerX--; moved = true; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) { playerX++; moved = true; }

        if (moved && client != null && client.isOpen()) {
            client.send("MOVE " + playerX + "," + playerY);
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (client != null) client.close();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MMO Client");
        config.setWindowedMode(800, 600);
        new Lwjgl3Application(new GameClient(), config);
    }
}

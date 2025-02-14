package dev.mixsource.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameRenderer {

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private int playerX, playerY;
    private final int cellSize;

    public GameRenderer(int cellSize) {
        this.cellSize = cellSize;
        this.playerX = 0;
        this.playerY = 0;
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(playerX * cellSize, playerY * cellSize, 0);
        camera.update();
    }

    public void render() {
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(playerX * cellSize, playerY * cellSize, cellSize, cellSize);
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
} 
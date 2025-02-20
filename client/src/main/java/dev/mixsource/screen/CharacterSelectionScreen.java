package dev.mixsource.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.core.GameClient;
import dev.mixsource.factories.ConnectorFactory;
import dev.mixsource.model.CharacterModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CharacterSelectionScreen implements Screen {
    private final Game game;
    private final Stage stage;
    private final Skin skin;

    private Set<CharacterModel> characters;
    private String userToken;

    public CharacterSelectionScreen(final Game game, final Skin skin) {
        this.game = game;
        this.skin = skin;
        this.stage = new Stage(new FitViewport(1280, 720));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        final Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("Selecione um Personagem", skin);
        title.setColor(Color.WHITE);
        table.add(title).colspan(3).padBottom(20);
        table.row();

        table.add(new Label("Nome", skin)).pad(10);
        table.add(new Label("Nível", skin)).pad(10);
        table.add(new Label("Ação", skin)).pad(10);
        table.row();

        for (CharacterModel ch : characters) {
            Label nameLabel = new Label(ch.getName(), skin);
            Label levelLabel = new Label("Nível: " + ch.getLevel(), skin);

            TextButton selectButton = new TextButton("Selecionar", skin);
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log(this.getClass().getName(), "Entrando no mundo com o personagem: " + ch.getName());
                    final WebSocketServerConnector connector = ConnectorFactory.createGameConnector(userToken);
                    connector.connect();
                    while (!connector.isConnected()) {
                        Gdx.app.log(this.getClass().getName(), "Aguardando conexão com o servidor...");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Gdx.app.error(this.getClass().getName(), "Erro ao aguardar conexão com o servidor: " + e.getMessage());
                        }
                    }
                    final GameScreen gameScreen = ((GameClient) game).getGameScreen();
                    gameScreen.setConnector(connector);
                    gameScreen.setPlayerCharacter(ch);
                    game.setScreen(gameScreen);
                }
            });

            table.add(nameLabel).pad(10);
            table.add(levelLabel).pad(10);
            table.add(selectButton).pad(10);
            table.row();
        }
        stage.addActor(table);
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
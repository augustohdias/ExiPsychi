package dev.mixsource.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.mixsource.model.Character;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.function.Consumer;


import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterSelectionScreen {

    private Stage stage;
    private Skin skin;
    private Table table;
    private List<Character> characters;
    private Consumer<Character> selectionListener;

    public CharacterSelectionScreen(List<Character> characters, Consumer<Character> listener) {
        this.characters = characters;
        this.selectionListener = listener;
        stage = new Stage(new ScreenViewport());
        skin = new Skin();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("default-white", new TextureRegionDrawable(new Texture(pixmap)));
        pixmap.dispose();

        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;

        Pixmap upPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        upPixmap.setColor(Color.DARK_GRAY);
        upPixmap.fill();
        TextureRegionDrawable upDrawable = new TextureRegionDrawable(new Texture(upPixmap));
        upPixmap.dispose();

        Pixmap downPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        downPixmap.setColor(Color.LIGHT_GRAY);
        downPixmap.fill();
        TextureRegionDrawable downDrawable = new TextureRegionDrawable(new Texture(downPixmap));
        downPixmap.dispose();

        textButtonStyle.up = upDrawable;
        textButtonStyle.down = downDrawable;
        textButtonStyle.over = upDrawable;
        skin.add("default", textButtonStyle);

        Gdx.input.setInputProcessor(stage);
        createUI();
    }

    private void createUI() {
        table = new Table();
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
        
        for (Character ch : characters) {
            Label nameLabel = new Label(ch.getName(), skin);
            Label levelLabel = new Label("Nível: " + ch.getLevel(), skin);
            
            TextButton selectButton = new TextButton("Selecionar", skin);
            selectButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    if (selectionListener != null) {
                        selectionListener.accept(ch);
                    }
                }
            });
            
            table.add(nameLabel).pad(10);
            table.add(levelLabel).pad(10);
            table.add(selectButton).pad(10);
            table.row();
        }
        stage.addActor(table);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
} 
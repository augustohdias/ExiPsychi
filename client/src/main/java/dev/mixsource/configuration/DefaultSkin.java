package dev.mixsource.configuration;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DefaultSkin {
    private static final String FONT_PATH = "assets/arial.ttf";
        private static Skin DEFAULT_CHARACTER_SELECTION_SKIN = null;
        private static Skin DEFAULT_LOGIN_SKIN = null;
    
        public static final TextureRegion playerTexture() {
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(0, 1, 0, 1);
            pixmap.fillRectangle(0, 0, 32, 32);
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            return new TextureRegion(texture);
        }

        public static final TextureRegion otherTexture() {
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 0, 0, 1);
            pixmap.fillRectangle(0, 0, 32, 32);
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            return new TextureRegion(texture);
        }

        public static final Skin defaultLogin() {
            if (Objects.nonNull(DEFAULT_LOGIN_SKIN)) {
                return DEFAULT_LOGIN_SKIN;
            }
            Skin skin = new Skin();
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 16;
            parameter.color = Color.WHITE;
            parameter.borderWidth = 1;
            parameter.borderColor = Color.BLACK;
            parameter.shadowOffsetX = 1;
            parameter.shadowOffsetY = 1;
            parameter.shadowColor = new Color(0, 0, 0, 0.75f);
            BitmapFont font = generator.generateFont(parameter);
            generator.dispose();
            skin.add("default-font", font);
    
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = font;
            labelStyle.fontColor = Color.WHITE;
            skin.add("default", labelStyle);
    
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
    
            DEFAULT_LOGIN_SKIN = skin;
            return DEFAULT_LOGIN_SKIN;
        }
    
        public static final Skin defaultCharacterSelection() {
            if (Objects.nonNull(DEFAULT_CHARACTER_SELECTION_SKIN)) {
                return DEFAULT_CHARACTER_SELECTION_SKIN;
            }
            Skin skin = new Skin();

            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            skin.add("default-white", new TextureRegionDrawable(new Texture(pixmap)));
            pixmap.dispose();
    
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 16;
            parameter.color = Color.WHITE;
            parameter.borderWidth = 1;
            parameter.borderColor = Color.BLACK;
            parameter.shadowOffsetX = 1;
            parameter.shadowOffsetY = 1;
            parameter.shadowColor = new Color(0, 0, 0, 0.75f);
            BitmapFont font = generator.generateFont(parameter);
            generator.dispose();
            
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
            DEFAULT_CHARACTER_SELECTION_SKIN = skin;
            return DEFAULT_CHARACTER_SELECTION_SKIN;
    }

    public static BitmapFont createNameFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        
        parameter.size = 16;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = new Color(0, 0, 0, 0.75f);
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        
        return font;
    }
}

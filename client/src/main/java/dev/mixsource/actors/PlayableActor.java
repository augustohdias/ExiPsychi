package dev.mixsource.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;

import dev.mixsource.model.Action;
import dev.mixsource.model.CharacterModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayableActor extends Actor {
    private final CharacterModel character;
    private final TextureRegion characterTexture;
    private final BitmapFont nameFont;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private static final float NAME_OFFSET_Y = 10f;
    private static final float SPRITE_SCALE = 1.5f;
    public static final int CELL_SIZE = 32;
    
    private Vector2 serverPosition = new Vector2();
    private Vector2 predictedPosition = new Vector2();
    private float interpolationAlpha;

    private static final float MOVEMENT_SPEED = 200f; // pixels/segundo

    public PlayableActor(final CharacterModel character, final BitmapFont nameFont, final TextureRegion characterTexture) {
        this.character = character;
        this.characterTexture = characterTexture;
        this.nameFont = nameFont;
                
        setSize(characterTexture.getRegionWidth() * SPRITE_SCALE, 
                characterTexture.getRegionHeight() * SPRITE_SCALE);
        updatePositionFromModel();
    }
    
    private void updatePositionFromModel() {
        float gridX = character.getX().floatValue() * CELL_SIZE;
        float gridY = character.getY().floatValue() * CELL_SIZE;
        
        setPosition(
            gridX - getWidth()/2, 
            gridY - getHeight()/2
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (interpolationAlpha < 1) {
            interpolationAlpha += delta * 10;
            setPosition(
                predictedPosition.x * (1 - interpolationAlpha) + serverPosition.x * interpolationAlpha,
                predictedPosition.y * (1 - interpolationAlpha) + serverPosition.y * interpolationAlpha
            );
        }
        
        if (character.getAction() == Action.MOVE) {
            float moveX = 0, moveY = 0;
            switch (character.getDirection()) {
                case UP: moveY = MOVEMENT_SPEED * delta; break;
                case DOWN: moveY = -MOVEMENT_SPEED * delta; break;
                case LEFT: moveX = -MOVEMENT_SPEED * delta; break;
                case RIGHT: moveX = MOVEMENT_SPEED * delta; break;
            }
            setPosition(getX() + moveX, getY() + moveY);
        }
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(characterTexture,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());

        nameFont.getData().setScale(1.0f);
        nameFont.setColor(1f, 1f, 1f, 1f);

        String name = character.getName();
        glyphLayout.setText(nameFont, name);
        float textX = getX() + (getWidth() - glyphLayout.width) / 2;
        float textY = getY() + getHeight() + NAME_OFFSET_Y + glyphLayout.height;

        nameFont.setColor(0f, 0f, 0f, 0.5f);
        nameFont.draw(batch, name, textX + 1, textY - 1);

        nameFont.setColor(1f, 1f, 1f, 1f);
        nameFont.draw(batch, name, textX, textY);
    }

    public void syncWithModel(CharacterModel updatedCharacter) {
        float newX = updatedCharacter.getX().floatValue() * CELL_SIZE;
        float newY = updatedCharacter.getY().floatValue() * CELL_SIZE;
        
        newX -= getWidth()/2;
        newY -= getHeight()/2;
        
        serverPosition.set(newX, newY);
        predictedPosition.set(getX(), getY());
        interpolationAlpha = 0f;
        
        this.character.setDirection(updatedCharacter.getDirection());
        this.character.setAction(updatedCharacter.getAction());
    }

    public void dispose() {
        if (nameFont != null) {
            nameFont.dispose();
        }
    }
}

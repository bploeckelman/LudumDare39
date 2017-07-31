package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;

public abstract class GameObject {

    public float animStateTime;
    public Vector2 position;

    public Rectangle bounds;
    protected float bounds_offset_x;
    protected float bounds_offset_y;

    public Rectangle collisionBounds;
    protected float collision_offset_x;
    protected float collision_offset_y;

    public TextureRegion keyframe;
    public GameScreen gameScreen;
    public boolean dead = false;
    public boolean remove = false;

    public GameObject(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        position = new Vector2();
        bounds = new Rectangle();
        collisionBounds = new Rectangle();
    }

    protected void setKeyFrame(TextureRegion textureRegion) {
        keyframe = textureRegion;

        setSize(keyframe.getRegionWidth(), keyframe.getRegionHeight());
    }

    public void setX(float x) {
        position.x = x;
        bounds.x = position.x - bounds_offset_x;
        collisionBounds.x = position.x - collision_offset_x;
    }

    public void setY(float y) {
        position.y = y;
        bounds.y = position.y - bounds_offset_y;
        collisionBounds.y = position.y - collision_offset_y;
    }

    public void setLocation(float x, float y) {
        setX(x);
        setY(y);
    }

    public void setSize(float width, float height) {
        bounds.width = width;
        bounds.height = height;

        updateCollisionBounds(bounds);

        bounds_offset_x = width / 2;
        bounds_offset_y = height /2;
    }

    protected void updateCollisionBounds(Rectangle bounds) {
        collisionBounds.set(bounds);
        collision_offset_x = collisionBounds.width / 2;
        collision_offset_y = collisionBounds.height / 2;
    }

    public void setBoundsLocation(float x, float y) {
        setLocation(x + bounds_offset_x, y + bounds_offset_y);
    }

    public void update(float dt) {
        animStateTime += dt;
    }

    public void render(SpriteBatch batch) {
        bounds.x = position.x - bounds_offset_x;
        bounds.y = position.y - bounds_offset_y;

        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
        if (GameScreen.DEBUG) {
            Assets.defaultNinePatch.draw(batch, collisionBounds.x, collisionBounds.y, collisionBounds.width, collisionBounds.height);
        }    }
}

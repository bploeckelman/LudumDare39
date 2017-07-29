package lando.systems.ld39.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class PlayerCar extends GameObject {

    // this is the bounds the car can move around
    public Rectangle constraintBounds;

    public float speed = 0;

    private float bounds_offset_x = 10f;
    private float bounds_offset_y = 10f;
    private static float anim_frame_duration = 0.1f;

    private Animation<TextureRegion> anim;
    private float animStateTime;

    // TODO: addon layers

    public PlayerCar() {
        super();

        animStateTime = 0f;
        anim = new Animation<TextureRegion>(anim_frame_duration, new TextureRegion(Assets.carBase));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        keyframe = anim.getKeyFrame(animStateTime);

        bounds.width = keyframe.getRegionWidth();
        bounds.height = keyframe.getRegionHeight();
        bounds_offset_x = bounds.width / 2;
        bounds_offset_y = bounds.height / 2;

        position.x = (Config.gameWidth  - bounds.width) / 2f;
        position.y = (Config.gameHeight - bounds.height) / 2f;
    }

    @Override
    public void update(float dt) {
        animStateTime += dt;
        keyframe = anim.getKeyFrame(animStateTime);

        updatePosition();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y);
    }

    // movement
    private void updatePosition() {
        bounds.x = position.x - bounds_offset_x;
        bounds.y = position.y - bounds_offset_y;

        float offset = 10;
        if (isUp()) {
            bounds.y += offset;
        } else if (isDown()) {
            bounds.y -= offset;
        }

        if (isRight()) {
            bounds.x += offset;
        } else if (isLeft()) {
            bounds.x -= offset;
        }

        constrainBounds(bounds);

        // update position (center)
        position.x = bounds.x + bounds_offset_x;
        position.y = bounds.y + bounds_offset_y;

        setSpeed();
    }

    private void setSpeed() {
        speed = 1000 * (position.y - constraintBounds.y) / constraintBounds.height;
    }

    private void constrainBounds(Rectangle bounds) {
        if (constraintBounds == null) return;

        if (bounds.x < constraintBounds.x) {
            bounds.x = constraintBounds.x;
        }

        if (bounds.y < constraintBounds.y) {
            bounds.y = constraintBounds.y;
        }

        if (bounds.x + bounds.width > constraintBounds.x + constraintBounds.width) {
            bounds.x = constraintBounds.x + constraintBounds.width - bounds.width;
        }

        if (bounds.y + bounds.height > constraintBounds.y + constraintBounds.height) {
            bounds.y = constraintBounds.y + constraintBounds.height - bounds.height;
        }
    }

    private boolean isUp() {
        return isPressed(Input.Keys.W, Input.Keys.UP, Input.Keys.DPAD_UP);
    }

    private boolean isPressed(int... keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDown() {
        return isPressed(Input.Keys.S, Input.Keys.DOWN, Input.Keys.DPAD_DOWN);
    }

    private boolean isLeft() {
        return isPressed(Input.Keys.A, Input.Keys.LEFT, Input.Keys.DPAD_LEFT);
    }

    private boolean isRight() {
        return isPressed(Input.Keys.D, Input.Keys.RIGHT, Input.Keys.DPAD_RIGHT);
    }
}

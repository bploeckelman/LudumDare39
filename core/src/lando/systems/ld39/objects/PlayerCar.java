package lando.systems.ld39.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class PlayerCar extends Vehicle {

    // this is the bounds the car can move around
    public Rectangle constraintBounds;

    public float speed = 0;

    private float bounds_offset_x = 10f;
    private float bounds_offset_y = 10f;

    private Animation<TextureRegion> coil;

    // TODO: addon layers

    public PlayerCar(GameScreen gameScreen) {
        super(gameScreen, Assets.carBase);

        bounds_offset_x = bounds.width / 2;
        bounds_offset_y = bounds.height / 2;

        position.x = (Config.gameWidth  - bounds.width) / 2f;
        position.y = (Config.gameHeight - bounds.height) / 2f;

        loadImages();
    }

    private void loadImages() {
        coil = new Animation<TextureRegion>(0.1f, Assets.atlas.findRegions("coil"), Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

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
        // i can't drive 55
        speed = 10 + (10 * (position.y - constraintBounds.y) / constraintBounds.height);
        int tires = tiresOffRoad();
        speed *= .5f + (.125 * ( 4 - tires));

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

    @Override
    public void render(SpriteBatch batch) {
        renderTires(batch);
        renderChassis(batch);
        renderPower(batch);
        renderWeapons(batch);
    }

    private void renderTires(SpriteBatch batch) {

    }

    private void renderChassis(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y);
        // draw damage
    }

    private void renderPower(SpriteBatch batch) {
        renderBattery(batch);
        renderBoosters(batch);
    }

    private void renderBattery(SpriteBatch batch) {

    }

    private void renderBoosters(SpriteBatch batch) {

    }

    private void renderWeapons(SpriteBatch batch) {

    }


}

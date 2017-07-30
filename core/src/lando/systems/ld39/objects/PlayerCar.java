package lando.systems.ld39.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class PlayerCar extends Vehicle {

    public static float minSpeed = 2;

    public Upgrades upgrades = new Upgrades();

    // this is the bounds the car can move around
    public Rectangle constraintBounds;

    public float speed = 0;
    public float maxSpeed = 15;

    public boolean isBoosting = false;

    public float batteryLevel;
    public float maxBattery;

    private float bounds_offset_x = 10f;
    private float bounds_offset_y = 10f;

    private Animation<TextureRegion> coil;
    private Animation<TextureRegion> smallBooster;
    private Animation<TextureRegion> largeBooster;

    public boolean dead = false;

    // TODO: addon layers

    public PlayerCar(GameScreen gameScreen) {
        super(gameScreen, Assets.carBase);
        // TODO: make this based on battery upgrade
        maxBattery = 10;
        batteryLevel = maxBattery;
        bounds_offset_x = bounds.width / 2;
        bounds_offset_y = bounds.height / 2;

        position.x = (Config.gameWidth  - bounds.width) / 2f;
        position.y = (Config.gameHeight - bounds.height) / 2f;
        bounds.x = position.x - bounds_offset_x;
        bounds.y = position.y - bounds_offset_y;

        loadImages();
    }

    private void loadImages() {
        coil = new Animation<TextureRegion>(0.1f, Assets.atlas.findRegions("CoilAnim"), Animation.PlayMode.LOOP);
        smallBooster = new Animation<TextureRegion>(0.1f, Assets.atlas.findRegions("BoostersSmallAnim"), Animation.PlayMode.LOOP);
        largeBooster = new Animation<TextureRegion>(0.1f, Assets.atlas.findRegions("BoostersLargeAnim"), Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (batteryLevel <= 0){
            speed = 0;
            dead = true;
            return;
        }

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

        testSetUpgradesAndRemoveThisMethod();

        setSpeed();
        updateBattery(dt);
        offroadSlowdown();
    }

    private void testSetUpgradesAndRemoveThisMethod() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            upgrades.setNext(Upgrades.Battery);

            if (!isBoosting) {
                isBoosting = true;
            } else {
                isBoosting = false;
                upgrades.setNext(Upgrades.Booster);
            }

            upgrades.setNext(Upgrades.Engine);
            upgrades.setNext(Upgrades.Wheels);
            upgrades.setNext(Upgrades.Chassis);
            upgrades.setNext(Upgrades.Damage);
        }
    }

    public void setUpgrade(int type, int level) {
        upgrades.setLevel(type, level);
    }

    private void updateBattery(float dt){
        batteryLevel -= (speed/maxSpeed) * dt;
        if (batteryLevel < 2){
            speed *= (.5f * batteryLevel);
        }
    }

    // Moved this after the battery, so we still use the full speed to reduce the battery
    private void offroadSlowdown(){
        int tires = tiresOffRoad();
        speed *= .5f + (.125 * ( 4 - tires));
    }

    Vector3 tempVector3 = new Vector3();
    private void setSpeed() {
        // i can't drive 55
        gameScreen.camera.project(tempVector3.set(position.x, position.y, 0));
        float screenPercent = tempVector3.y / gameScreen.camera.viewportHeight;
        speed = minSpeed + ((maxSpeed - minSpeed) * (screenPercent));
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
        bounds.x = position.x - bounds_offset_x;
        bounds.y = position.y - bounds_offset_y;
        render(batch, Upgrades.Wheels, batteryLevel > 0);
        renderChassis(batch);
        render(batch, Upgrades.Battery, batteryLevel > 4);
        render(batch, Upgrades.Booster, isBoosting);
    }

    private void render(SpriteBatch batch, int item, boolean animate) {
        TextureRegion image = upgrades.getCurrentImage(item, animStateTime, animate);
        if (image != null) {
            batch.draw(image, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void renderChassis(SpriteBatch batch) {
        render(batch, Upgrades.Chassis, false);
        render(batch, Upgrades.Damage, false);
    }
}
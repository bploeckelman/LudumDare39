package lando.systems.ld39.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Config;

public class PlayerCar extends Vehicle {

    public static float minSpeed = 2;

    // this is the bounds the car can move around
    public Rectangle constraintBounds;

    public float speed = 0;
    public float maxSpeed = 15;

    public float batteryLevel;
    public float maxBattery;

    public boolean dead = false;

    // TODO: addon layers

    public PlayerCar(GameScreen gameScreen) {
        super(gameScreen, Item.Chassis);

        // TODO: make this based on battery upgrade
        maxBattery = 10;
        batteryLevel = maxBattery;

        position.x = (Config.gameWidth  - bounds.width) / 2f;
        position.y = (Config.gameHeight - bounds.height) / 2f;


        // set all upgrades to 0
        upgrades.setLevel(Item.Engine, 0);
        upgrades.setLevel(Item.Battery, 0);
        upgrades.setLevel(Item.Wheels, 0);
        upgrades.setLevel(Item.Booster, 0);
        upgrades.setLevel(Item.Chassis, 0);
        upgrades.setLevel(Item.Damage, 0);
        upgrades.setLevel(Item.Weapons, 0);
        upgrades.setLevel(Item.Axes, 0);
    }

    @Override
    public boolean isBoosting() {
        return isPressed(Input.Keys.SPACE);
    }

    private int axeHits = 0;
    public void pickupAxe() {
        axeHits = 0;
        setUpgrade(Item.Axes, 1);
    }

    public void hitAxe() {
        if (++axeHits == 3) {
            setUpgrade(Item.Axes, 0);
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (batteryLevel <= 0){
            speed = 0;
            dead = true;
            return;
        }

        updateFire(dt);

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

    private float fireTime = 0;

    @Override
    public boolean isFiring() {
        return fireTime > 0;
    }

    private void updateFire(float dt) {
        if (fireTime > 0) {
            fireTime -= dt;
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            fireTime = 2;
        }
    }

    private void testSetUpgradesAndRemoveThisMethod() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            upgrades.setNext(Item.Battery);
            upgrades.setNext(Item.Booster);
            upgrades.setNext(Item.Engine);
            upgrades.setNext(Item.Wheels);
            upgrades.setNext(Item.Chassis);
            upgrades.setNext(Item.Damage);
            upgrades.setNext(Item.Weapons);
            upgrades.setNext(Item.Axes);
        }
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

    public float getBatteryPercent(){
        return batteryLevel/maxBattery;
    }

    public float getHealthPercent(){
        return .5f;
    }
}
package lando.systems.ld39.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class PlayerCar extends Vehicle {

    public static float minSpeed = 20;
//    public static Rectangle defaultCollisionBounds = new Rectangle(8, 15, 38, 80);

    // this is the bounds the car can move around
    public Rectangle constraintBounds;

    public float speed = 0;
    public float maxSpeed = 500;

    public float handling = 200;
    public float maxHandling;

    public float batteryLevel;
    public float maxBattery;

    private Vector2 bulletVelocity;
    private Vector2 bulletPosition;
    private boolean alternateGun;

    public static IntMap<Array<Upgrades.UpgradeItemMeta>> upgradesMeta;

    // TODO: addon layers

    public PlayerCar(GameScreen gameScreen) {
        super(gameScreen, Item.Chassis);

        initializeUpgradesMeta();

        bulletVelocity = new Vector2();
        bulletPosition = new Vector2();
        alternateGun = true;
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

        setStatsBasedOnUpgradeLevels();
    }

    public float getSpeed() {
        float currentSpeed = speed;
        if (isBoosting()) {
            currentSpeed *= 10;
        }
        return currentSpeed;
    }

    @Override
    public void addDamage(float damage) {
        super.addDamage(damage);

        int damageLevel = (int)(3 * (1 - health/maxHealth));
        if (damageLevel > 2) {
            damageLevel = 2;
        }

        setUpgrade(Item.Damage, damageLevel);
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
    protected void updateCollisionBounds(Rectangle bounds) {
        collisionBounds.set(bounds);
        Assets.inflateRect(collisionBounds, -4, -10);
        collision_offset_x = collisionBounds.width / 2;
        collision_offset_y = collisionBounds.height / 2;
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
        UpdateBoost(dt);

        bounds.x = position.x - bounds_offset_x;
        bounds.y = position.y - bounds_offset_y;

        float offset = 200f * dt;
        if (isUp()) {
            bounds.y += offset;
        } else if (isDown()) {
            bounds.y -= offset;
        }

        float horiz_offset = handling * dt;
        if (isRight()) {
            bounds.x += horiz_offset;
        } else if (isLeft()) {
            bounds.x -= horiz_offset;
        }

        constrainBounds(bounds);

        // update position (center)
        position.x = bounds.x + bounds_offset_x;
        position.y = bounds.y + bounds_offset_y;


        testSetUpgradesAndRemoveThisMethod();

        setSpeed();
        updateBattery(dt);
        offRoadSlowdown(dt);
        updateCollisionBounds(bounds);

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

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && isWeaponEnabled()) {
            bulletVelocity.set(0, getSpeed() + 1000);
            if (upgrades.getLevel(Item.Weapons) == 1){
                alternateGun = !alternateGun;
                bulletPosition.set(position.x + 6, position.y + bounds_offset_y);
                if (alternateGun){
                    bulletPosition.add(-12, 0);
                }
                gameScreen.addBullet(this, bulletVelocity, bulletPosition, Assets.basicProjectileTex, 1);
                fireTime = .1f;
            } else if (upgrades.getLevel(Item.Weapons) == 2){
                bulletPosition.set(position.x, position.y + bounds_offset_y);
                gameScreen.addBullet(this, bulletVelocity, bulletPosition, Assets.zappaTex, 5);
                fireTime = .3f;
            } else if (upgrades.getLevel(Item.Weapons) == 3){
                bulletPosition.set(position.x, position.y + bounds_offset_y);
                gameScreen.addBullet(this, bulletVelocity, bulletPosition, Assets.missileTex, 10);
                fireTime = .5f;

            }

        }
    }


    private float boostTime = -100;

    @Override
    public boolean isBoosting() {
        return boostTime > 0;
    }

    private void UpdateBoost(float dt) {
        boostTime -= dt;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            int boostLevel = upgrades.getLevel(Item.Booster);

            // five second cool down
            if (boostTime < -5) {
                boostTime = boostLevel * 3;
            }
        }
    }

    private boolean isWeaponEnabled() {
        return upgrades.getLevel(Item.Weapons) > 0;
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

            int stop = 0;
        }
    }

    private void updateBattery(float dt){
        batteryLevel -= (speed/maxSpeed) * dt;
        if (batteryLevel < 2){
            speed *= (.5f * batteryLevel);
        }
    }

    // Moved this after the battery, so we still use the full speed to reduce the battery
    private void offRoadSlowdown(float dt){
        int tires = tiresOffRoad(dt);
        speed *= .5f + (.125 * ( 4 - tires));
    }

    Vector3 tempVector3 = new Vector3();
    private void setSpeed() {
        // i can't drive 55
        gameScreen.camera.project(tempVector3.set(position.x, position.y, 0));
        float screenPercent = tempVector3.y / (gameScreen.camera.viewportHeight * 0.62f);
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
        return batteryLevel / maxBattery;
    }

    public void setStatsBasedOnUpgradeLevels() {
        maxBattery = upgradesMeta.get(Item.Battery).get(upgrades.getLevel(Item.Battery)).value;
        batteryLevel = maxBattery;

        maxHealth = upgradesMeta.get(Item.Chassis).get(upgrades.getLevel(Item.Chassis)).value;
        health = maxHealth;

        maxSpeed = upgradesMeta.get(Item.Booster).get(upgrades.getLevel(Item.Booster)).value;
        speed = 0;

        maxHandling = upgradesMeta.get(Item.Wheels).get(upgrades.getLevel(Item.Wheels)).value;
        handling = maxHandling;
    }

    private void initializeUpgradesMeta() {
        if (upgradesMeta != null) return;

        Array<Upgrades.UpgradeItemMeta> batteryUpgradeMeta = new Array<Upgrades.UpgradeItemMeta>();
        batteryUpgradeMeta.addAll(
                new Upgrades.UpgradeItemMeta(0, 10),
                new Upgrades.UpgradeItemMeta(100, 50),
                new Upgrades.UpgradeItemMeta(200, 100),
                new Upgrades.UpgradeItemMeta(400, 200)
        );
        Array<Upgrades.UpgradeItemMeta> engineUpgradeMeta = new Array<Upgrades.UpgradeItemMeta>();
        engineUpgradeMeta.addAll(
                new Upgrades.UpgradeItemMeta(0, 50),
                new Upgrades.UpgradeItemMeta(100, 100),
                new Upgrades.UpgradeItemMeta(150, 150),
                new Upgrades.UpgradeItemMeta(250, 250)
        );
        Array<Upgrades.UpgradeItemMeta> boosterUpgradeMeta = new Array<Upgrades.UpgradeItemMeta>();
        boosterUpgradeMeta.addAll(
                new Upgrades.UpgradeItemMeta(0, 500),
                new Upgrades.UpgradeItemMeta(100, 1000),
                new Upgrades.UpgradeItemMeta(300, 1500)
        );
        Array<Upgrades.UpgradeItemMeta> weaponsUpgradeMeta = new Array<Upgrades.UpgradeItemMeta>();
        weaponsUpgradeMeta.addAll(
                new Upgrades.UpgradeItemMeta(0, 0),
                new Upgrades.UpgradeItemMeta(100, 0),
                new Upgrades.UpgradeItemMeta(200, 0),
                new Upgrades.UpgradeItemMeta(400, 0)
        );
        Array<Upgrades.UpgradeItemMeta> chassisUpgradeMeta = new Array<Upgrades.UpgradeItemMeta>();
        chassisUpgradeMeta.addAll(
                new Upgrades.UpgradeItemMeta(0, 100),
                new Upgrades.UpgradeItemMeta(100, 200),
                new Upgrades.UpgradeItemMeta(200, 400)
        );
        Array<Upgrades.UpgradeItemMeta> wheelsUpgradeMeta = new Array<Upgrades.UpgradeItemMeta>();
        wheelsUpgradeMeta.addAll(
                new Upgrades.UpgradeItemMeta(0, 200),
                new Upgrades.UpgradeItemMeta(150, 300),
                new Upgrades.UpgradeItemMeta(300, 400)
        );

        upgradesMeta = new IntMap<Array<Upgrades.UpgradeItemMeta>>();
        upgradesMeta.put(Item.Engine, engineUpgradeMeta);
        upgradesMeta.put(Item.Battery, batteryUpgradeMeta);
        upgradesMeta.put(Item.Wheels, wheelsUpgradeMeta);
        upgradesMeta.put(Item.Booster, boosterUpgradeMeta);
        upgradesMeta.put(Item.Chassis, chassisUpgradeMeta);
        upgradesMeta.put(Item.Weapons, weaponsUpgradeMeta);
    }

}
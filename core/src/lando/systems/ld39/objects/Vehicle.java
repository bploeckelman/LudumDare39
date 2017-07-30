package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntIntMap;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Config;

public class Vehicle extends GameObject {

    public float tireOffset_x = 21;
    public float tireOffset_y = 18;

    public float health = 10;
    public float maxHealth = 10;

    public Rectangle collisionBounds;

    public static Rectangle defaultCollisionBounds = new Rectangle(8, 15, 38, 80);

    protected float animStateTime;
    protected Upgrades upgrades = new Upgrades();

    protected float bounds_offset_x;
    protected float bounds_offset_y;
    public float bulletDamage = 4;
    public float reloadTime = 2;


    // TODO: addon layers

    public Vehicle(GameScreen gameScreen, int chassisType) {
        super(gameScreen);
        collisionBounds = new Rectangle();

        upgrades.setLevel(chassisType, 0);
        keyframe = upgrades.getCurrentImage(chassisType, 0, false);

        setSize(keyframe.getRegionWidth(), keyframe.getRegionHeight());
        setLocation((Config.gameWidth - bounds.width) / 2f, (Config.gameHeight - bounds.height) / 2f);
    }

    public void setX(float x) {
        position.x = x;
        bounds.x = position.x - bounds_offset_x;
        collisionBounds.x = bounds.x + defaultCollisionBounds.x;
    }

    public void setY(float y) {
        position.y = y;
        bounds.y = position.y - bounds_offset_y;
        collisionBounds.y = bounds.y + defaultCollisionBounds.y;
    }

    public void setLocation(float x, float y) {
        setX(x);
        setY(y);
    }

    public void setSize(float width, float height) {
        bounds.width = width;
        bounds.height = height;
        collisionBounds.width = defaultCollisionBounds.width;
        collisionBounds.height = defaultCollisionBounds.height;
        bounds_offset_x = width / 2;
        bounds_offset_y = height /2;
    }

    public void setBoundsLocation(float x, float y) {
        setLocation(x + bounds_offset_x, y + bounds_offset_y);
    }

    // set individual upgrade - doesn't have to exist - if you set an item to a level, it'll get added to the car
    public void setUpgrade(int type, int level) {
        upgrades.setLevel(type, level);
    }

    public IntIntMap getUpgrades() {
        return upgrades.getUpgrades();
    }

    public void setUpgrades(IntIntMap upgradeItems) {
        upgrades.setUpgrades(upgradeItems);
    }

    @Override
    public void update(float dt) {
        animStateTime += dt;
        if (health <= 0 ){
            dead = true;
        }
    }

    public boolean isRunning() {
        return true;
    }

    public boolean isBoosting() {
        return false;
    }

    public boolean isFiring() {
        return false;
    }

    @Override
    public void render(SpriteBatch batch) {
        bounds.x = position.x - bounds_offset_x;
        bounds.y = position.y - bounds_offset_y;

        boolean isRunning = isRunning();

        render(batch, Item.Wheels, isRunning);
        render(batch, Item.Chassis, false);
        render(batch, Item.Damage, false);
        render(batch, Item.Battery, isRunning);
        render(batch, Item.Engine, isRunning);
        render(batch, Item.Booster, isBoosting() && isRunning);
        render(batch, Item.Axes, true);
        render(batch, Item.Weapons, isFiring());
    }

    protected void render(SpriteBatch batch, int item, boolean animate) {
        TextureRegion image = upgrades.getCurrentImage(item, animStateTime, animate);
        if (image != null) {
            batch.draw(image, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public int tiresOffRoad(){
        int count = 0;
        if (!gameScreen.road.isOnRoad(position.x - tireOffset_x, position.y - tireOffset_y)) {
            gameScreen.particleSystem.addDirtParticles(position.x - tireOffset_x, position.y - tireOffset_y);
            count++;
        }
        if (!gameScreen.road.isOnRoad(position.x + tireOffset_x, position.y - tireOffset_y)) {
            gameScreen.particleSystem.addDirtParticles(position.x + tireOffset_x, position.y - tireOffset_y);
            count++;
        }
        if (!gameScreen.road.isOnRoad(position.x - tireOffset_x, position.y + tireOffset_y)) {
            gameScreen.particleSystem.addDirtParticles(position.x - tireOffset_x, position.y + tireOffset_y);
            count++;
        }
        if (!gameScreen.road.isOnRoad(position.x + tireOffset_x, position.y + tireOffset_y)) {
            gameScreen.particleSystem.addDirtParticles(position.x + tireOffset_x, position.y + tireOffset_y);
            count++;
        }
        return count;
    }

    public float getHealthPercent(){
        return health / maxHealth;
    }

}

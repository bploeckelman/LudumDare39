package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntIntMap;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class Vehicle extends GameObject {

    public float tireOffset_x = 21;
    public float tireOffset_y = 18;

    public float health = 10;
    public float maxHealth = 10;

    protected Upgrades upgrades = new Upgrades();

    public float bulletDamage = 4;
    public float reloadTime = 2;


    // TODO: addon layers

    public Vehicle(GameScreen gameScreen, int chassisType) {
        super(gameScreen);
        collisionBounds = new Rectangle();

        upgrades.setLevel(chassisType, 0);
        setKeyFrame(upgrades.getCurrentImage(chassisType, 0, false));

        setLocation((Config.gameWidth - bounds.width) / 2f, (Config.gameHeight - bounds.height) / 2f);
    }

    public void addDamage(float damage) {
        float currentHealth = health - damage;
        health = MathUtils.clamp(currentHealth, 0, maxHealth);
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
        super.update(dt);

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

    public boolean isExploded() {
        return dead;
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

        if (isExploded()) {
            render(batch, Item.Explosions, true);
        }
        if (GameScreen.DEBUG) {
            Assets.defaultNinePatch.draw(batch, collisionBounds.x, collisionBounds.y, collisionBounds.width, collisionBounds.height);
        }

    }

    protected void render(SpriteBatch batch, int item, boolean animate) {
        TextureRegion image = upgrades.getCurrentImage(item, animStateTime, animate);
        if (image != null) {
            batch.draw(image, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public int tiresOffRoad(float dt){
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
        health -= count * 5f * dt;
        return count;
    }

    public float getHealthPercent(){
        return health / maxHealth;
    }

}

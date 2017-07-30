package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Config;

public class Vehicle extends GameObject {

    public float tireOffset_x = 21;
    public float tireOffset_y = 18;

    protected float animStateTime;
    protected Upgrades upgrades = new Upgrades();

    protected float bounds_offset_x;
    protected float bounds_offset_y;

    // TODO: addon layers

    public Vehicle(GameScreen gameScreen, int chassisType) {
        super(gameScreen);

        upgrades.setLevel(chassisType, 0);
        keyframe = upgrades.getCurrentImage(chassisType, 0, false);

        bounds.width = keyframe.getRegionWidth();
        bounds_offset_x = bounds.width / 2;

        bounds.height = keyframe.getRegionHeight();
        bounds_offset_y = bounds.height / 2;

        setLocation((Config.gameWidth - bounds.width) / 2f, (Config.gameHeight - bounds.height) / 2f);

        bounds.x = position.x - bounds_offset_x;
        bounds.y = position.y - bounds_offset_y;
    }

    public void setX(float x) {
        position.x = x;
        bounds.x = position.x - bounds_offset_x;
    }

    public void setY(float y) {
        position.y = y;
        bounds.y = position.y - bounds_offset_y;
    }

    public void setLocation(float x, float y) {
        setX(x);
        setY(y);
    }

    @Override
    public void update(float dt) {
        animStateTime += dt;
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
}

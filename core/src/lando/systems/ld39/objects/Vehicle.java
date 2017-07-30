package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Config;

public class Vehicle extends GameObject {

    public float tireOffset_x = 21;
    public float tireOffset_y = 18;

    public float health = 10;
    public float maxHealth = 10;

    protected float anim_frame_duration = 0.1f;

    private Animation<TextureRegion> anim;
    protected float animStateTime;

    // TODO: addon layers

    public Vehicle(GameScreen gameScreen, TextureRegion vehicleRegion) {
        super(gameScreen);

        keyframe = setTexture(vehicleRegion);

        bounds.width = keyframe.getRegionWidth();
        bounds.height = keyframe.getRegionHeight();


        position.x = (Config.gameWidth  - bounds.width) / 2f;
        position.y = (Config.gameHeight - bounds.height) / 2f;
    }

    protected TextureRegion setTexture(TextureRegion texture) {
        animStateTime = 0f;
        anim = new Animation<TextureRegion>(anim_frame_duration, new TextureRegion(texture));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim.getKeyFrame(animStateTime);
    }

    @Override
    public void update(float dt) {
        animStateTime += dt;
        keyframe = anim.getKeyFrame(animStateTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y);
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

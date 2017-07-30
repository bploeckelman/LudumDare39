package lando.systems.ld39.objects;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;

/**
 * Created by Brian on 7/30/2017.
 */

public class EnemyCar extends Vehicle {

    private int chassis;
    public float relSpeed = 2f; // Relative speed
    private float deadTimer;

    public EnemyCar(GameScreen gameScreen) {
        this(gameScreen, Item.EnemyChassis1);
    }

    public EnemyCar(GameScreen gameScreen, int enemyChassis) {
        super(gameScreen, enemyChassis);
        chassis = enemyChassis;
        deadTimer = 4;
        setUpgrade(Item.Explosions, MathUtils.random(Item.getMaxLevel(Item.Explosions)));
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (dead){
            // TODO: use explosion textures instead
            deadTimer -= dt;
            if (deadTimer < 0){
                remove = true;
            }
            return;
        }
        tiresOffRoad();
        Rectangle playerBounds = gameScreen.playerCar.bounds;
        Vector2 playerPosition = gameScreen.playerCar.position;
        if (collisionBounds.overlaps(gameScreen.playerCar.collisionBounds)) {
            // TODO: Collide
        } else {
            // Follow player
            float distance = position.dst(playerPosition);
            Vector2 direction = (new Vector2(position)).sub(playerPosition).nor();

            position.add(- (direction.x * relSpeed), (gameScreen.playerCar.speed *dt)- (direction.y * relSpeed));
            setLocation(position.x, position.y);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
//        batch.setColor(Color.BLUE);
//        batch.draw(Assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);
//        batch.setColor(Color.WHITE);

        render(batch, Item.EnemyChassis1, true);
        super.render(batch);
    }

    private static int testlevel = 0;
    public static Vehicle getEnemy(GameScreen gameScreen) {
        //Vector3 cameraPosition = gameScreen.camera.position;

        int chassis = Item.EnemyChassis1;

        //int level = MathUtils.random(Item.getMaxLevel(chassis));
        int newLevel = testlevel++;
        if (testlevel == Item.getMaxLevel(chassis)) {
            testlevel = 0;
        }

        Vehicle enemyCar = new EnemyCar(gameScreen, chassis);
        enemyCar.setUpgrade(chassis, newLevel);

        Vector2 position = gameScreen.playerCar.position;
        // adjust initial starting point
        enemyCar.setLocation(position.x, position.y - gameScreen.playerCar.bounds.height - 100);

        return enemyCar;
    }
}

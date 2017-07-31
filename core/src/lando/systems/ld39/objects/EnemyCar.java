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

        setRandom(Item.Explosions);
        setRandom(Item.Engine);
        setRandom(Item.Booster);
    }

    private void setRandom(int item) {
        int max = Item.getMaxLevel(item);
        if (max != -1) {
            setUpgrade(item, MathUtils.random(max));
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (dead){
            deadTimer -= dt;
            if (deadTimer < 0){
                remove = true;
            }
            return;
        }
        tiresOffRoad(dt);
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

        render(batch, chassis, true);
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
        float positionY = gameScreen.camera.position.y + gameScreen.camera.viewportHeight/2f + enemyCar.bounds_offset_y;
        if (newLevel == 1){
            positionY = gameScreen.camera.position.y - gameScreen.camera.viewportHeight/2f - enemyCar.bounds_offset_y;
        }
        float left = gameScreen.road.getLeftEdge(positionY);
        float right = gameScreen.road.getRightEdge(positionY);
        float positionX = MathUtils.random(left + enemyCar.bounds_offset_x, right - enemyCar.bounds_offset_x);


        enemyCar.setLocation(positionX, positionY);


        return enemyCar;
    }
}

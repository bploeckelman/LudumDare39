package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.screens.GameScreen;

/**
 * Created by Brian on 7/30/2017.
 */

public class EnemyCar extends Vehicle {

    private int chassis;
    public float relSpeed = 3f; // Relative speed
    private float deadTimer;
    private boolean collidedWithPlayer = false;
    private float collidedWithPlayerTimer;
    private static float collidedWithPlayerTimerDefault = 1f;
    private Vector2 collisionDirection;

    public EnemyCar(GameScreen gameScreen) {
        this(gameScreen, Item.EnemyChassis1);
    }

    public EnemyCar(GameScreen gameScreen, int enemyChassis) {
        super(gameScreen, enemyChassis);
        chassis = enemyChassis;
        deadTimer = 4;
        collisionDirection = new Vector2();
        collidedWithPlayerTimer = collidedWithPlayerTimerDefault;

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

        if (collidedWithPlayer) {
            collidedWithPlayerTimer -= dt;
            if (collidedWithPlayerTimer < 0) {
                collidedWithPlayer = false;
                collidedWithPlayerTimer = collidedWithPlayerTimerDefault;
            } else {
                if (collisionDirection.x != 0) {
                    position.add(-(collisionDirection.x * relSpeed), gameScreen.playerCar.speed * dt);
                } else {
                    position.add(0, (gameScreen.playerCar.speed * dt) - (collisionDirection.y * relSpeed));
                }
                setLocation(position.x, position.y);
            }
        } else if (collisionBounds.overlaps(gameScreen.playerCar.collisionBounds)) {
            // TODO: Collide
            if (!collidedWithPlayer) {
                Rectangle intersection = new Rectangle();
                Intersector.intersectRectangles(collisionBounds, gameScreen.playerCar.collisionBounds, intersection);
                if (intersection.width > intersection.height) {
                    if (intersection.y > collisionBounds.y) {
                        // Intersects with top
                        collisionDirection.set(0, 1);
                    } else {
                        // Intersects with bottom
                        collisionDirection.set(0, -1);
                    }
                } else {
                    if(intersection.x > collisionBounds.x) {
                        // Intersects with right
                        collisionDirection.set(1, 0);
                    } else {
                        // Intersects with left
                        collisionDirection.set(-1, 0);
                    }
                }

                collidedWithPlayer = true;
            }
        } else {
            // Follow player
            float distance = position.dst(playerPosition);
            Vector2 direction = (new Vector2(position)).sub(playerPosition).nor();

            position.add(- (direction.x * relSpeed), (gameScreen.playerCar.speed * dt)- (direction.y * relSpeed));
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

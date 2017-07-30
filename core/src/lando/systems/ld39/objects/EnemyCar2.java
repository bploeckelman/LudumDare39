package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.screens.GameScreen;

/**
 * Created by Brian on 7/30/2017.
 */

public class EnemyCar2 extends Vehicle {

    private int chassis;

    public EnemyCar2(GameScreen gameScreen) {
        this(gameScreen, Item.EnemyChassis1);
    }

    public EnemyCar2(GameScreen gameScreen, int enemyChassis) {
        super(gameScreen, enemyChassis);
        chassis = enemyChassis;
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

        Vehicle enemyCar = new EnemyCar2(gameScreen, chassis);
        enemyCar.setUpgrade(chassis, newLevel);

        Vector2 position = gameScreen.playerCar.position;
        // adjust initial starting point
        enemyCar.setLocation(position.x, position.y);

        return enemyCar;
    }
}

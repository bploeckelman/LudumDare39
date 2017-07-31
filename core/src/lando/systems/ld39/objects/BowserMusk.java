package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;

/**
 * Created by dsgraham on 7/31/17.
 */
public class BowserMusk extends GameObject {

    public Vector2 offsetPosition;

    public BowserMusk(GameScreen gameScreen) {
        super(gameScreen);
        offsetPosition = new Vector2();
        bounds.set(0,0, 20, 20);
    }

    public void update(float dt) {
        animStateTime += dt;
        position.y += gameScreen.playerCar.speed * dt;
    }

    public void render(SpriteBatch batch) {
        bounds.x = position.x - bounds.width/2 + offsetPosition.x;
        bounds.y = position.y - bounds.height/2 + offsetPosition.y;

        batch.draw(Assets.bowserMusk.getKeyFrame(animStateTime), bounds.x, bounds.y, bounds.width, bounds.height);
        if (GameScreen.DEBUG) {
            Assets.defaultNinePatch.draw(batch, collisionBounds.x, collisionBounds.y, collisionBounds.width, collisionBounds.height);
        }
    }
}

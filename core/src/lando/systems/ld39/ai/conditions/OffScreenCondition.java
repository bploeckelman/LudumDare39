package lando.systems.ld39.ai.conditions;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.objects.EnemyCar;

/**
 * Created by dsgraham on 7/31/17.
 */
public class OffScreenCondition extends Condition {
    Vector3 screenPos;
    float margin;
    Rectangle rect;

    public OffScreenCondition(EnemyCar owner) {
        super(owner);
        screenPos = new Vector3();
        margin = 100;
        rect = new Rectangle();
    }

    @Override
    public boolean isTrue() {
        owner.gameScreen.camera.project(screenPos.set(owner.position, 0));
        rect.set(-margin, -margin, owner.gameScreen.camera.viewportWidth + 2*margin, owner.gameScreen.camera.viewportHeight + 2* margin);
        return !rect.contains(screenPos.x, screenPos.y);
    }
}

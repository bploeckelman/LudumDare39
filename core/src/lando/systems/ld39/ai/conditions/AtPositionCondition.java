package lando.systems.ld39.ai.conditions;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.objects.EnemyCar;

/**
 * Created by dsgraham on 7/31/17.
 */
public class AtPositionCondition extends Condition {

    float offset;
    Vector3 screenPos;

    public AtPositionCondition(EnemyCar owner, float position) {
        super(owner);
        offset = position;
        screenPos = new Vector3();

    }

    @Override
    public boolean isTrue() {
        owner.gameScreen.camera.project(screenPos.set(owner.position, 0));
        return MathUtils.isEqual(offset, screenPos.y, 10);
    }
}

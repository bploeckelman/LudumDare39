package lando.systems.ld39.ai.conditions;

import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.objects.GameObject;

/**
 * Created by dsgraham on 7/30/17.
 */
public class BelowCondition extends Condition {
    public BelowCondition(EnemyCar owner) {
        super(owner);
    }

    @Override
    public boolean isTrue() {
        return owner.position.y < owner.gameScreen.playerCar.bounds.y;
    }
}

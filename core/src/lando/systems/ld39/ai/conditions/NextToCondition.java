package lando.systems.ld39.ai.conditions;

import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.objects.GameObject;

/**
 * Created by dsgraham on 7/30/17.
 */
public class NextToCondition extends Condition {
    public NextToCondition(EnemyCar owner) {
        super(owner);
    }

    @Override
    public boolean isTrue() {
        return  owner.position.y > owner.gameScreen.playerCar.bounds.y &&
                owner.position.y < owner.gameScreen.playerCar.bounds.y + owner.gameScreen.playerCar.bounds.height;
    }
}

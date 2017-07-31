package lando.systems.ld39.ai.conditions;

import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.objects.GameObject;

/**
 * Created by dsgraham on 7/30/17.
 */
public class AheadCondition extends Condition {
    public AheadCondition(EnemyCar owner) {
        super(owner);
    }

    @Override
    public boolean isTrue() {
        return  owner.position.y > owner.gameScreen.playerCar.bounds.y + owner.gameScreen.playerCar.bounds.height &&
                owner.position.x > owner.gameScreen.playerCar.bounds.x &&
                owner.position.x < owner.gameScreen.playerCar.bounds.x + owner.gameScreen.playerCar.bounds.width;
    }
}

package lando.systems.ld39.ai.conditions;

import lando.systems.ld39.objects.EnemyCar;

/**
 * Created by dsgraham on 7/30/17.
 */
public class NotBehindCondition extends Condition {
    public NotBehindCondition(EnemyCar owner) {
        super(owner);
    }

    @Override
    public boolean isTrue() {
        return  owner.position.y < owner.gameScreen.playerCar.bounds.y &&
                owner.position.x <= owner.gameScreen.playerCar.bounds.x &&
                owner.position.x >= owner.gameScreen.playerCar.bounds.x + owner.gameScreen.playerCar.bounds.width;
    }
}

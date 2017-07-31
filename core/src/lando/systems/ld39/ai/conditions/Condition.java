package lando.systems.ld39.ai.conditions;

import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.objects.GameObject;

/**
 * Created by dsgraham on 7/30/17.
 */
public abstract class Condition {
    EnemyCar owner;

    public Condition(EnemyCar owner){
        this.owner = owner;
    }

    // find a way to make this a condition check

    public abstract boolean isTrue();
}

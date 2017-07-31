package lando.systems.ld39.ai.conditions;

import lando.systems.ld39.objects.EnemyCar;

/**
 * Created by dsgraham on 7/31/17.
 */
public class HealthBelowCondition extends Condition{

    float percent;

    public HealthBelowCondition(EnemyCar owner, float percent) {
        super(owner);
        this.percent = percent;
    }

    @Override
    public boolean isTrue() {
        return (owner.health/owner.maxHealth) < percent;
    }
}

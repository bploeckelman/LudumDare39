package lando.systems.ld39.ai.states;

import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.objects.GameObject;

/**
 * Created by dsgraham on 7/30/17.
 */
public abstract class State {
    EnemyCar owner;

    public State(EnemyCar owner){
        this.owner = owner;
    }

    public abstract void update(float dt);
    public abstract void onEnter();
    public abstract void onExit();

}

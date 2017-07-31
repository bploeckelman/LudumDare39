package lando.systems.ld39.ai;

import lando.systems.ld39.ai.conditions.Condition;
import lando.systems.ld39.ai.states.State;

/**
 * Created by dsgraham on 7/30/17.
 */
public class Transition {
    State from;
    Condition condition;
    State to;

    public Transition(State from, Condition condition, State to){
        this.from = from;
        this.condition = condition;
        this.to = to;
    }
}

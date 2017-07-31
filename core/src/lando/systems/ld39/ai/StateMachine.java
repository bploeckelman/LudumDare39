package lando.systems.ld39.ai;

import com.badlogic.gdx.utils.Array;
import lando.systems.ld39.ai.states.State;

/**
 * Created by dsgraham on 7/30/17.
 */
public class StateMachine {
    Array<Transition> transitions;
    State current;

    public StateMachine(State start, Array<Transition> transitions) {
        this.current = start;
        this.transitions = transitions;
        this.current.onEnter();
    }

    public void addTransition(Transition tran){
        transitions.add(tran);
    }

    public void update(float dt) {
        current.update(dt);

        State nextState = getNextState();
        if (nextState != null){
            current.onExit();
            nextState.onEnter();
            current = nextState;
        }
    }

    public State getNextState() {
        for(Transition transition : transitions) {
            if (transition.from != null && !transition.from.equals(current)){
                // Not the right transition from
                continue;
            }

            if (transition.condition.isTrue()){
                return transition.to;
            }

        }
        return null;
    }
}

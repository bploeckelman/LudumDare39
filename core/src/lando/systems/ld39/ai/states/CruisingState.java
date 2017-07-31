package lando.systems.ld39.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.objects.GameObject;

/**
 * Created by dsgraham on 7/30/17.
 */
public class CruisingState extends State {
    private float cruiseSlowDown;

    public CruisingState(EnemyCar owner) {
        super(owner);
        cruiseSlowDown = MathUtils.random(.6f, .9f);
    }

    @Override
    public void update(float dt) {
        float carSpeed = Math.max(owner.gameScreen.playerCar.speed * cruiseSlowDown, 200f);
        float positionY = owner.position.y + carSpeed * dt;
        float topOfCar = positionY + owner.bounds.height/2f;
        float testPosition = topOfCar + 5f;
        float left = owner.gameScreen.road.getLeftEdge(testPosition);
        float right = owner.gameScreen.road.getRightEdge(testPosition);
        float deltaX = 0;
        if (owner.position.x - owner.bounds.width/2 <= left) {deltaX += 4; }
        if (owner.position.x + owner.bounds.width/2 >= right) {deltaX += -4; }

        left = owner.gameScreen.road.getLeftEdge(topOfCar);
        right = owner.gameScreen.road.getRightEdge(topOfCar);

        if (owner.position.x - owner.bounds.width/2 <= left) {deltaX += 2; }
        if (owner.position.x + owner.bounds.width/2 >= right) {deltaX += -2; }

        float positionX = owner.position.x + deltaX;


        owner.setLocation(positionX, positionY);
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }
}

package lando.systems.ld39.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.objects.EnemyCar;

/**
 * Created by dsgraham on 7/30/17.
 */
public class FollowState extends State {

    float followDistance;

    public FollowState(EnemyCar owner) {
        super(owner);
        followDistance = MathUtils.random(150, 200);
    }

    @Override
    public void update(float dt) {
        float deltaY = (owner.gameScreen.playerCar.position.y - followDistance) - owner.position.y;
        float speedMul = 1;
        float positionY = 0;
        if (deltaY < - 20){
            speedMul = .9f + (deltaY/1000f);
        } else if (deltaY > 20){
            speedMul = 1.2f;
        } else if (deltaY < 0){
            positionY = -.2f;
        } else if (deltaY > 0){
            positionY = .2f;
        }


        float carSpeed = Math.max(owner.gameScreen.playerCar.speed * speedMul, 100f);
        positionY += owner.position.y + carSpeed * dt;
        float deltaX = owner.avoidGrass(positionY);

        if (deltaY > -50) {
            if (owner.position.x < owner.gameScreen.playerCar.position.x) {
                deltaX += .2f;
            } else {
                deltaX -= .2f;
            }
        }

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

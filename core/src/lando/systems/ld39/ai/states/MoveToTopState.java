package lando.systems.ld39.ai.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.utils.Assets;

/**
 * Created by dsgraham on 7/31/17.
 */
public class MoveToTopState extends State {

    Vector3 screenPos;

    public MoveToTopState(EnemyCar owner) {
        super(owner);

        screenPos = new Vector3();
        owner.invincible = true;
    }

    @Override
    public void update(float dt) {
        owner.gameScreen.camera.project(screenPos.set(owner.position, 0));
        float deltaY = 500 - screenPos.y;
        float positionY = 0;

        if (deltaY < 0){
            positionY = -1;
        } else if (deltaY > 0){
            positionY = 1;
        }


        float carSpeed = Math.max(owner.gameScreen.playerCar.speed, 100f);
        positionY += owner.position.y + carSpeed * dt;
        float deltaX = owner.avoidGrass(positionY);

        if (deltaY > -50) {
            if (owner.position.x < owner.gameScreen.playerCar.position.x) {
                deltaX += .3f;
            } else {
                deltaX -= .3f;
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
        owner.invincible = false;
        Gdx.app.log("Boss", "Say Something");
    }
}

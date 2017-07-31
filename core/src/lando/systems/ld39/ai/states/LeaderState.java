package lando.systems.ld39.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.utils.Assets;

/**
 * Created by dsgraham on 7/31/17.
 */
public class LeaderState extends State {

    float reloadDelay;
    Vector2 bulletPosition;
    Vector2 bulletVelocity;
    Vector3 screenPos;

    public LeaderState(EnemyCar owner) {
        super(owner);

        reloadDelay = 1;
        bulletPosition = new Vector2();
        bulletVelocity = new Vector2();
        screenPos = new Vector3();
    }

    @Override
    public void update(float dt) {
        reloadDelay -= dt;
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

        if (Math.abs(deltaY) < 10 &&
            owner.position.x > owner.gameScreen.playerCar.bounds.x &&
            owner.position.x < owner.gameScreen.playerCar.bounds.x + owner.gameScreen.playerCar.bounds.width &&
            reloadDelay < 0){

            reloadDelay = 1;
            owner.gameScreen.addBullet(owner, bulletVelocity.set(0, carSpeed - 500),
                    bulletPosition.set(owner.position.x, positionY - owner.bounds.height/2f),
                    Assets.basicProjectileTex, 5);
            owner.gameScreen.addBullet(owner, bulletVelocity.set(-200, carSpeed - 500),
                    bulletPosition.set(owner.position.x, positionY - owner.bounds.height/2f),
                    Assets.basicProjectileTex, 5);

            owner.gameScreen.addBullet(owner, bulletVelocity.set(200, carSpeed - 500),
                    bulletPosition.set(owner.position.x, positionY - owner.bounds.height/2f),
                    Assets.basicProjectileTex, 5);


        }
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }
}

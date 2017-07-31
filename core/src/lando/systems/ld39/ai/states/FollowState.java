package lando.systems.ld39.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.SoundManager;

/**
 * Created by dsgraham on 7/30/17.
 */
public class FollowState extends State {

    float followDistance;
    float reloadDelay;
    Vector2 bulletVelocity;
    Vector2 bulletPosition;
    float carSpeed;

    public FollowState(EnemyCar owner) {
        super(owner);
        followDistance = MathUtils.random(150, 250);
        reloadDelay = 1;
        bulletPosition = new Vector2();
        bulletVelocity = new Vector2();
        carSpeed = owner.gameScreen.playerCar.speed;
    }

    @Override
    public void update(float dt) {
        reloadDelay -= dt;
        float deltaY = (owner.gameScreen.playerCar.position.y - followDistance) - owner.position.y;
        float speedMul = 1;
        float positionY = 0;
        if (deltaY < - 200){
            speedMul = 1 + (deltaY/1000f);
        } else if (deltaY > 20){
            speedMul = 1.2f;
        } else if (deltaY < 0){
            positionY = -.2f;
        } else if (deltaY > 0){
            positionY = .2f;
        }


        carSpeed = MathUtils.lerp(carSpeed, Math.max(owner.gameScreen.playerCar.speed * speedMul, 400f), .1f);
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

        if (owner.position.y < owner.gameScreen.playerCar.bounds.y &&
            owner.position.x > owner.gameScreen.playerCar.bounds.x &&
            owner.position.x < owner.gameScreen.playerCar.bounds.x + owner.gameScreen.playerCar.bounds.width &&
            reloadDelay < 0){
            reloadDelay = 1;
            owner.gameScreen.addBullet(owner, bulletVelocity.set(0, carSpeed + 500),
                    bulletPosition.set(owner.position.x, positionY + owner.bounds.height/2f),
                    Assets.basicProjectileTex, 5);
            SoundManager.playSound(SoundManager.SoundOptions.gunshot);
        }
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }
}

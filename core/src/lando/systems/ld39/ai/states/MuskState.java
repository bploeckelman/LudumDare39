package lando.systems.ld39.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.objects.Item;
import lando.systems.ld39.utils.Assets;

/**
 * Created by dsgraham on 7/31/17.
 */
public class MuskState extends State {

    Vector3 screenPos;
    boolean headRight;
    float reloadTimer;
    Vector2 bulletPosition;
    Vector2 bulletVelocity;

    public MuskState(EnemyCar owner) {
        super(owner);
        screenPos = new Vector3();
        headRight = false;
        reloadTimer = 1;
        bulletPosition = new Vector2();
        bulletVelocity = new Vector2();
    }

    @Override
    public void update(float dt) {
        reloadTimer -= dt;
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

        float deltaX = 0;
        float moveamount = MathUtils.lerp(2, 5, 1f -(owner.health/owner.maxHealth));
        if (headRight){
            if (owner.position.x > 700) headRight = !headRight;
            deltaX += moveamount;
        } else {
            if (owner.position.x < 100) headRight = !headRight;
            deltaX -= moveamount;
        }



        float positionX = owner.position.x + deltaX;


        owner.setLocation(positionX, positionY);

        if (reloadTimer < 0){
            reloadTimer = 1;
            int bullets = (int)MathUtils.lerp(20, 4, (owner.health/(owner.maxHealth/2f)));
            float deltaR = 360f/bullets;
            int randomRot = MathUtils.random(180);
            bulletPosition.set(owner.position.x, owner.position.y);
            for (int i = 0; i < bullets; i++){
                bulletVelocity.set(MathUtils.sinDeg(i * (deltaR) + randomRot), MathUtils.cosDeg(i * (deltaR)  + randomRot));
                bulletVelocity.scl(300);
                bulletVelocity.add(0, owner.gameScreen.playerCar.speed);
                owner.gameScreen.addBullet(owner, bulletVelocity, bulletPosition, Assets.basicProjectileTex, 5);
            }
        }
    }

    @Override
    public void onEnter() {
        owner.setUpgrade(Item.EnemyChassis1, 6);
        owner.flying = true;
        owner.invincible = false;
    }

    @Override
    public void onExit() {

    }
}

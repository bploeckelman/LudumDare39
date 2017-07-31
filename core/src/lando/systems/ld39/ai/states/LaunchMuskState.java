package lando.systems.ld39.ai.states;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.objects.BowserMusk;
import lando.systems.ld39.objects.EnemyCar;
import lando.systems.ld39.objects.Item;
import lando.systems.ld39.objects.Vehicle;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.accessors.RectangleAccessor;
import lando.systems.ld39.utils.accessors.Vector2Accessor;

/**
 * Created by dsgraham on 7/31/17.
 */
public class LaunchMuskState extends State {

    Vector3 screenPos;

    public LaunchMuskState(EnemyCar owner) {
        super(owner);
        screenPos = new Vector3();

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


        float carSpeed = owner.gameScreen.playerCar.speed;
        positionY += owner.position.y + carSpeed * dt;
        float deltaX = owner.avoidGrass(positionY);


        if (owner.position.x < owner.gameScreen.playerCar.position.x) {
            deltaX += .3f;
        } else {
            deltaX -= .3f;
        }


        float positionX = owner.position.x + deltaX;


        owner.setLocation(positionX, positionY);
    }

    @Override
    public void onEnter() {
        owner.setUpgrade(Item.EnemyChassis1, 5 );
        owner.invincible = true;
        final BowserMusk b = new BowserMusk(owner.gameScreen);
        Timeline.createParallel()
            .push(Tween.to(b.offsetPosition, Vector2Accessor.XY, 5)
                    .target(100, 200)
                    .waypoint(- 200, -200)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            b.remove = true;
                            Vehicle deadTruck = EnemyCar.getEnemy(owner.gameScreen);
                            deadTruck.setUpgrade(Item.EnemyChassis1, 5);
                            deadTruck.setLocation(owner.position.x, owner.position.y);
                            deadTruck.health = 0;
                            owner.gameScreen.vehicles.add(deadTruck);
                            owner.setLocation(owner.position.x, owner.position.y + 300);
                        }
                    }))
            .push(Tween.to(b.bounds, RectangleAccessor.WH, 4)
                    .target(80, 80)
                    .waypoint(250,250))
            .start(Assets.tween);


        b.setLocation(owner.position.x, owner.position.y);
        owner.gameScreen.gameObjects.add(b);
    }

    @Override
    public void onExit() {

    }
}

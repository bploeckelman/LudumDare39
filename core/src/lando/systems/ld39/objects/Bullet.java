package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;

/**
 * Created by dsgraham on 7/30/17.
 */
public class Bullet implements Pool.Poolable{
    public Vector2 position;
    public Vector2 speed;
    public Vehicle owner;
    public float damage;
    public boolean alive;


    public Bullet(){
        speed = new Vector2();
        position = new Vector2();
    }

    public void init(Vector2 pos, Vector2 spd, Vehicle owner){
        position.set(pos);
        speed.set(spd);
        this.owner = owner;
        this.damage = owner.bulletDamage;
        alive = true;
    }

    public void update(float dt){
        position.add(speed.x * dt, speed.y * dt);

    }

    public void render(SpriteBatch batch){
        batch.draw(Assets.whitePixel, position.x, position.y, 2, 2);
    }

    @Override
    public void reset() {
        position.set(0, 0);
        speed.set(0,0);
        owner = null;
        alive = false;
    }
}

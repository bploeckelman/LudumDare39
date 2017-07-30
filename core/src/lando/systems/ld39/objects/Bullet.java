package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by dsgraham on 7/30/17.
 */
public class Bullet implements Pool.Poolable{
    public Vector2 position;
    public Vector2 speed;
    public Vehicle owner;
    public boolean alive;


    public Bullet(){
        position = new Vector2();
        speed = new Vector2();
    }

    public void init(Vector2 pos, Vector2 spd, Vehicle owner){
        position.set(pos);
        speed.set(spd);
        this.owner = owner;
        alive = true;
    }

    public void update(float dt){
        position.add(speed.x * dt, speed.y * dt);

    }

    public void render(SpriteBatch batch){
        
    }

    @Override
    public void reset() {
        position.set(0, 0);
        speed.set(0,0);
        owner = null;
        alive = false;
    }
}

package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by dsgraham on 7/30/17.
 */
public class Bullet {
    public Vector2 position;
    public Vector2 speed;
    public Vehicle owner;


    public Bullet(){

    }

    public void update(float dt){
        position.add(speed.x * dt, speed.y * dt);

    }

    public void render(SpriteBatch batch){
        
    }
}

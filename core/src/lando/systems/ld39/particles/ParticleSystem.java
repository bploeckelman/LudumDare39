package lando.systems.ld39.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld39.utils.Assets;

/**
 * Created by dsgraham on 7/29/17.
 */
public class ParticleSystem {

    private final Array<Particle> activeParticles = new Array<Particle>();
    private final Pool<Particle> particlePool = Pools.get(Particle.class, 500);


    public ParticleSystem() {
        
    }


    public void addDirtParticles(float x, float y){
        int particles = 30;
        for (int i = 0; i < particles; i++){
                Particle part = particlePool.obtain();

                float speed = MathUtils.random(400f);
                float dir = MathUtils.random(360f);
                float px = x + MathUtils.random(-3f, 3f);
                float py = y + MathUtils.random(-3f, 3f);
                float vx = MathUtils.sinDeg(dir) * speed;
                float vy = MathUtils.cosDeg(dir) * speed;
                float scale = MathUtils.random(1, 4f);
                float ttl = MathUtils.random(.05f, .1f);
                part.init(
                        px, py,
                        vx, vy,
                        -vx, -vy, .5f,
                        .41f, .2f, .1f, 1f,
                        .3f, .1f, .05f, .5f,
                        scale, ttl,
                        new TextureRegion(Assets.whitePixel));

                activeParticles.add(part);
        }
    }

    public void update(float dt){
        int len = activeParticles.size;
        for (int i = len -1; i >= 0; i--){
            Particle part = activeParticles.get(i);
            part.update(dt);
            if (part.timeToLive <= 0){
                activeParticles.removeIndex(i);
                particlePool.free(part);
            }
        }
    }

    public void render(SpriteBatch batch){
        for (Particle part : activeParticles){
            part.render(batch);
        }
    }


    public void clearParticles(){
        particlePool.freeAll(activeParticles);
        activeParticles.clear();
    }
}

package lando.systems.ld39.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class PlayerCar extends GameObject {

    private static float bounds_offset_x = 10f;
    private static float bounds_offset_y = 10f;
    private static float anim_frame_duration = 0.1f;

    private Animation<TextureRegion> anim;
    private float animStateTime;

    // TODO: addon layers

    public PlayerCar() {
        super();

        animStateTime = 0f;
        anim = new Animation<TextureRegion>(anim_frame_duration, new TextureRegion(Assets.carBase));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        keyframe = anim.getKeyFrame(animStateTime);

        // TODO: placeholder until we add the actual car image and manually figure out it's bounds
        bounds.width = keyframe.getRegionWidth() - 2f * bounds_offset_x;
        bounds.height = keyframe.getRegionHeight() - 2f * bounds_offset_y;

        position.x = Config.gameWidth / 2f - bounds.width / 2f;
        position.y = Config.gameHeight / 2f - bounds.height / 2f;
        bounds.x = position.x + bounds_offset_x;
        bounds.y = position.y + bounds_offset_y;
    }

    @Override
    public void update(float dt) {
        bounds.x = position.x + bounds_offset_x;
        bounds.y = position.y + bounds_offset_y;

        animStateTime += dt;
        keyframe = anim.getKeyFrame(animStateTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        batch.setColor(1f, 0f, 0f, 0.5f);
        batch.draw(Assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(1f, 1f, 1f, 1f);
    }

}

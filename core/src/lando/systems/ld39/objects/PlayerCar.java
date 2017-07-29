package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld39.utils.Assets;

public class PlayerCar extends GameObject {

    private static float bounds_offset_x = 10f;
    private static float bounds_offset_y = 10f;
    private static float anim_frame_duration = 0.1f;

    private Animation<TextureRegion> anim;
    private float animStateTime = 0f;

    // TODO: addon layers

    public PlayerCar() {
        super();

        // TODO: placeholder until we add the actual car image and manually figure out it's bounds
        bounds.width = keyframe.getRegionWidth() - 2f * bounds_offset_x;
        bounds.height = keyframe.getRegionHeight() - 2f * bounds_offset_y;

        anim = new Animation<TextureRegion>(anim_frame_duration, new TextureRegion(Assets.testTexture));
        anim.setPlayMode(Animation.PlayMode.LOOP);
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

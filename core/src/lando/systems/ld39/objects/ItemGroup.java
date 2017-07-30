package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld39.utils.Assets;

import java.util.HashMap;

/**
 * Created by Brian on 7/30/2017.
 */
public class ItemGroup {

    // this indicates no images for this group - do not create an image --None--.png

    public static final String None = "--None--";
    private int max;

    private String[] values;

    private HashMap<String, Animation<TextureRegion>> animations = new HashMap<String, Animation<TextureRegion>>();
    private HashMap<String, TextureRegion> images = new HashMap<String, TextureRegion>();

    public ItemGroup(String... items) {
        max = items.length;
        values = items;

        for (String value : values) {
            if (value == None) continue;

            TextureRegion image = Assets.atlas.findRegion(value);
            Array anims = Assets.atlas.findRegions(value + "Anim");

            if (image == null) {
                image = (TextureRegion)anims.first();
            }

            images.put(value, image);

            if (anims.size > 0) {
                animations.put(value, new Animation<TextureRegion>(0.1f, anims, Animation.PlayMode.LOOP));
            }
        }
    }

    public int getMaxLevel() {
        return max;
    }

    public TextureRegion getImageAt(int level, float animTimer, boolean animate) {
        String value = values[level];
        if (value == None) {
            return null;
        }

        TextureRegion image = images.get(value);
        Animation<TextureRegion> anims = animations.get(value);
        return (animate && anims != null) ? anims.getKeyFrame(animTimer) : image;
    }
}
package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntIntMap;

/**
 * Created by Brian on 7/29/2017.
 */

public class Upgrades {

    private IntIntMap upgrades = new IntIntMap();

    public TextureRegion getCurrentImage(int type, float animTimer, boolean animate) {
        int level = upgrades.get(type, -1);
        if (level == -1) return null;

        return Item.items.get(type).getImageAt(level, animTimer, animate);
    }

    public IntIntMap getUpgrades() {
        return new IntIntMap(upgrades);
    }

    public void setUpgrades(IntIntMap upgrades) {
        this.upgrades = new IntIntMap(upgrades);
    }

    public int getLevel(int type) {
        return upgrades.get(type, -1);
    }

    public void setLevel(int type, int level) {
        int max = getMaxLevel(type);
        if (max != -1) {
            // don't set level of an item that doesn't exist
            upgrades.put(type, MathUtils.clamp(level, 0, max));
        }
    }

    public int getMaxLevel(int type) {
        return Item.getMaxLevel(type);
    }

    // used in testing. don't count on this doing what you think it might
    public void setNext(int type) {
        int level = upgrades.get(type, -1);
        if (level != -1) {
            if (level++ == getMaxLevel(type)) {
                level = 0;
            }
            setLevel(type, level);
        }
    }
}

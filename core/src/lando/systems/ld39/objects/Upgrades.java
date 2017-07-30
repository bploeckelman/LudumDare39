package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import lando.systems.ld39.utils.Assets;

import java.util.HashMap;

/**
 * Created by Brian on 7/29/2017.
 */

public class Upgrades {

    public static final int Engine = 0;
    public static final int Battery = 1;
    public static final int Wheels = 2;
    public static final int Booster = 3;
    public static final int Chassis = 4;
    public static final int Damage = 5;

    private static final String None = "-None-";

    public static class Upgrade {
        public int current = 0;
        public int max;

        private String[] values;

        private HashMap<String, Animation<TextureRegion>> animations = new HashMap<String, Animation<TextureRegion>>();
        private HashMap<String, TextureRegion> images = new HashMap<String, TextureRegion>();

        public Upgrade(String... upgrades) {
            this.current = 0;
            this.max = upgrades.length;
            values = upgrades;

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

        public TextureRegion getCurrentImage(float animTimer, boolean animate) {
            String value = values[current];
            if (value == None) {
                return null;
            }

            TextureRegion image = images.get(value);
            Animation<TextureRegion> anims = animations.get(value);
            return (animate && anims != null) ? anims.getKeyFrame(animTimer) : image;
        }
    }

    private static IntMap<Upgrade> currentUpgrades = new IntMap<Upgrade>();
    static {
        currentUpgrades.put(Battery, new Upgrade(None, "MediumBattery", "LargeBattery", "Coil"));
        currentUpgrades.put(Engine, new Upgrade("SmallEngine", "MediumEngine", "MegaEngine"));
        currentUpgrades.put(Wheels, new Upgrade("Bronze", "Silver", "Gold"));
        currentUpgrades.put(Booster, new Upgrade(None, "BoostersSmall", "BoostersLarge"));
        currentUpgrades.put(Chassis, new Upgrade("CarBase1", "CarBase2", "CarBase3"));
        currentUpgrades.put(Damage, new Upgrade(None, "DamageSmall", "DamageLarge"));
    }

    public TextureRegion getCurrentImage(int type, float animTimer, boolean animate) {
        Upgrade upgrade = currentUpgrades.get(type);
        if (upgrade == null) {
            System.out.println("Missing image for " + type);
            return null;
        }

        return upgrade.getCurrentImage(animTimer, animate);
    }

    public void setLevel(int type, int level) {
        Upgrade upgrade = currentUpgrades.get(type);
        if (upgrade != null) {
            if (level < 0) {
                level = 0;
            }
            if (level > upgrade.max) {
                level = upgrade.max;
            }
            upgrade.current = level;
        }
    }

    public int getLevel(int type) {
        Upgrade upgrade = currentUpgrades.get(type);
        return (upgrade != null) ? upgrade.current : 0;
    }

    public int getMaxLevel(int type) {
        Upgrade upgrade = currentUpgrades.get(type);
        return (upgrade != null) ? upgrade.max : 0;
    }

    // used in testing. don't count on this doing what you think it might
    public void setNext(int type) {
        Upgrade upgrade = currentUpgrades.get(type);
        if (upgrade != null) {
            if (upgrade.current + 1 >= upgrade.max) {
                upgrade.current = 0;
            } else {
                upgrade.current++;
            }
        }
    }
}

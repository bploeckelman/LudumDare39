package lando.systems.ld39.objects;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by Brian on 7/29/2017.
 */

public class Upgrades {

    public static final int Engine = 0;
    public static final int Battery = 1;
    public static final int Wheels = 2;
    public static final int Booster = 3;

    public class Upgrade {
        public int current;
        public int max;

        public Upgrade(int current, int max) {
            this.current = current;
            this.max = max;
        }
    }

    private IntMap<Upgrade> currentUpgrades = new IntMap<Upgrade>();

    public Upgrades() {
        currentUpgrades.put(Battery, new Upgrade(0, 3));
        currentUpgrades.put(Engine, new Upgrade(0, 2));
        currentUpgrades.put(Wheels, new Upgrade(0, 2));
        currentUpgrades.put(Booster, new Upgrade(0, 2));
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
            if (++upgrade.current > upgrade.max) {
                upgrade.current = 0;
            }
        }
    }
}

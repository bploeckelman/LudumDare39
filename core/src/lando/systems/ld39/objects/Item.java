package lando.systems.ld39.objects;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by Brian on 7/30/2017.
 */
public class Item {
    public static final int Engine = 0;
    public static final int Battery = 1;
    public static final int Wheels = 2;
    public static final int Booster = 3;
    public static final int Chassis = 4;
    public static final int Damage = 5;
    public static final int Weapons = 6;
    public static final int Axes = 7;
    public static final int EnemyChassis1 = 8;

    private static final String None = ItemGroup.None;

    public static IntMap<ItemGroup> items = new IntMap<ItemGroup>();

    public static void load() {
        if (items.size > 0) return;

        items.put(Battery, new ItemGroup(None, "MediumBattery", "LargeBattery", "Coil"));
        items.put(Engine, new ItemGroup(None, "SmallEngine", "MediumEngine", "MegaEngine"));
        items.put(Wheels, new ItemGroup("Bronze", "Silver", "Gold"));
        items.put(Booster, new ItemGroup(None, "BoostersSmall", "BoostersLarge"));
        items.put(Chassis, new ItemGroup("CarBase1", "CarBase2", "CarBase3"));
        items.put(Damage, new ItemGroup(None, "DamageSmall", "DamageLarge"));
        items.put(Axes, new ItemGroup(None, "TireAxes"));
        items.put(Weapons, new ItemGroup(None, "BasicGun", "Zappa", "RocketLauncher"));
        items.put(EnemyChassis1, new ItemGroup("Enemy1", "Enemy2", "Enemy3"));
    }

    public static int getMaxLevel(int itemType) {
        ItemGroup group = items.get(itemType);
        return (group != null) ? group.getMaxLevel() : -1;
    }
}

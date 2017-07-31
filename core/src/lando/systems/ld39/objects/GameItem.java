package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.SoundManager;

/**
 * Created by Brian on 7/30/2017.
 */
public class GameItem extends GameObject {

    public static class ItemData {
        public TextureRegion image;
        public boolean isPickup;
        public boolean removeOnRunOver;
        public float runoverDamage;
        public float inRoadPercentage;
        public int pickupId;

        public float collisionLeft;
        public float collisionRight;

        public SoundManager.SoundOptions pickupSoundType;


        public ItemData(String textureImage, int pickupId, float inRoadPercentage, SoundManager.SoundOptions pickupSoundType) {
            this(textureImage, true, 0, true, inRoadPercentage, pickupSoundType, 0, 0);
            this.pickupId = pickupId;
        }


        public ItemData(String textureImage, boolean isPickup, float runoverDamage, boolean removeOnRunOver, float inRoadPercentage, SoundManager.SoundOptions pickupSoundType) {
            this(textureImage, isPickup, runoverDamage, removeOnRunOver, inRoadPercentage, pickupSoundType, 0, 0);
        }

        public ItemData(String textureImage, boolean isPickup, float runoverDamage, boolean removeOnRunOver, float inRoadPercentage, SoundManager.SoundOptions pickupSoundType, float left, float right) {
            this.image = Assets.atlas.findRegion(textureImage);
            this.isPickup = isPickup;
            this.runoverDamage = runoverDamage;
            this.removeOnRunOver = removeOnRunOver;
            this.inRoadPercentage = inRoadPercentage;
            this.collisionLeft = left;
            this.collisionRight = right;
            this.pickupSoundType = pickupSoundType;
            this.pickupId = 0;
        }
    }

    private static Array<ItemData> obstacles = new Array<ItemData>();
    private static Array<ItemData> pickups = new Array<ItemData>();

    public static final int Repair = 1;
    public static final int Money = 2;
    public static final int Battery = 3;
    public static final int Weapon = 4;

    public static void load() {
        if (obstacles.size > 0) return;

        obstacles.add(new ItemData("palmtree", false, 100, false, 0, SoundManager.SoundOptions.crash_1, 33, 54));
        obstacles.add(new ItemData("palmtree2", false, 100, false, 0, SoundManager.SoundOptions.crash_1, 25, 62));
        obstacles.add(new ItemData("palmtree3", false, 100, false, 0, SoundManager.SoundOptions.crash_1, 33, 52));
        obstacles.add(new ItemData("cone", false, 5, true, 0.9f, null));
//        obstacles.add(new ItemData("manholecover", false, 0, false, 1, null));
//        obstacles.add(new ItemData("barricade", false, 10, true, 0.9f, null));
//        obstacles.add(new ItemData("barricade2", false, 10, true, 0.9f, null));
        obstacles.add(new ItemData("treeA", false, 100, false, 0, SoundManager.SoundOptions.crash_1, 23, 42));
        obstacles.add(new ItemData("treeC", false, 100, false, 0, SoundManager.SoundOptions.crash_1, 22, 37));
        obstacles.add(new ItemData("treeD", false, 100, false, 0, SoundManager.SoundOptions.crash_1, 23, 42));
        obstacles.add(new ItemData("treeE", false, 100, false, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("treeF", false, 100, false, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("treeG", false, 100, false, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("treeH", false, 100, false, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("cactusA", false, 20, true, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("cactusC", false, 20, true, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("cactusD", false, 20, true, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("cactusE", false, 20, true, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("cactusF", false, 20, true, 0, SoundManager.SoundOptions.crash_1));
        obstacles.add(new ItemData("cactusG", false, 20, true, 0, SoundManager.SoundOptions.crash_1));

        pickups.add(new ItemData("repairPickup", Repair, 0.6f, SoundManager.SoundOptions.pickup_repair));
        pickups.add(new ItemData("moneyPickup", Money, 0.6f, SoundManager.SoundOptions.pickup_money));
        pickups.add(new ItemData("batteryPickup", Battery, 0.6f, SoundManager.SoundOptions.pickup_battery));
        pickups.add(new ItemData("weaponPickup", Weapon, 0.6f, SoundManager.SoundOptions.pickup_weapon));
    }

    private ItemData item;

    public GameItem(GameScreen gameScreen, boolean pickup) {
        super(gameScreen);

        item = pickup ? pickups.get(MathUtils.random.nextInt(pickups.size)) : obstacles.get(MathUtils.random.nextInt(obstacles.size));
        setKeyFrame(item.image);
        if (pickup) {
            Assets.inflateRect(bounds, 10);
        }
    }

    @Override
    protected void updateCollisionBounds(Rectangle bounds) {
        if (item.collisionRight > 0 || item.collisionLeft > 0) {
              bounds = new Rectangle(item.collisionLeft, 0, item.collisionRight - item.collisionLeft, bounds.height);
        }
        super.updateCollisionBounds(bounds);
    }

    public static void AddItem(GameScreen gameScreen) {
        //boolean pickup = MathUtils.random.nextFloat() > 0.92f;

        GameItem item = new GameItem(gameScreen, false);

        PlayerCar car = gameScreen.playerCar;

        // temp
        //car.health = car.maxHealth;
        //car.batteryLevel = car.maxBattery;
        // end temp

        float x;
        float y = gameScreen.camera.position.y + gameScreen.camera.viewportHeight;

        boolean inRoad = MathUtils.random.nextFloat() < item.item.inRoadPercentage;

        float left = gameScreen.road.getLeftEdge(y);
        float right = gameScreen.road.getRightEdge(y);
        float shoulderBuffer = gameScreen.road.shoulderWidth * 1.5f;
        if (inRoad) {
            x = left + ((right - left) *  MathUtils.random.nextFloat());
        } else {
            // move things that do more than 75% damage farther off road
            float padding = (item.item.inRoadPercentage == 0 && item.item.runoverDamage > (car.maxHealth * .75)) ? 30 : 0;

            if (MathUtils.randomBoolean()) {
                x = (left - padding) * MathUtils.random.nextFloat();
            } else {
                x = right + padding;
                x +=  ((gameScreen.camera.viewportWidth - x) * MathUtils.random.nextFloat());
            }
        }

        item.setLocation(x, y);
        gameScreen.gameObjects.add(item);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        PlayerCar car = gameScreen.playerCar;
        if (car.collisionBounds.overlaps(bounds)) {
            car.addDamage(item.runoverDamage);
            if (car.health <= 0 && item.pickupSoundType != null) {
                SoundManager.playSound(item.pickupSoundType);
            }
            if (item.removeOnRunOver) {
                handlePickup(car, item);
                remove = true;
            }
        } else {
            float bottomY = (gameScreen.camera.position.y - gameScreen.camera.viewportHeight) / 2;
            remove = ((position.y + bounds_offset_y) < bottomY);
        }
    }

    private void handlePickup(PlayerCar car, ItemData item) {
        if (!item.isPickup) return;
        if (item.pickupSoundType != null) {
            SoundManager.playSound(item.pickupSoundType);
        }
        gameScreen.roundStats.powerupsCollected += 1;
        switch (item.pickupId) {
            case Repair:
                car.addDamage(-10);
                break;
            case Money:
                gameScreen.roundStats.powerupsCollected -= 1;
                gameScreen.roundStats.moneyCollected += 10;
                break;
            case Battery:
                float level = car.batteryLevel + 10;
                if (level > car.maxBattery) {
                    level = car.maxBattery;
                }
                car.batteryLevel = level;
                break;
            case Weapon:
                car.pickupAxe();
                break;
        }
    }
}

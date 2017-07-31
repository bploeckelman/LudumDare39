package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;

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

        public ItemData(String textureImage, int pickupId, float inRoadPercentage) {
            this(textureImage, true, 0, true, inRoadPercentage);
            this.pickupId = pickupId;
        }

        public ItemData(String textureImage, boolean isPickup, float runoverDamage, boolean removeOnRunOver, float inRoadPercentage) {
            image = Assets.atlas.findRegion(textureImage);
            this.isPickup = isPickup;
            this.runoverDamage = runoverDamage;
            this.removeOnRunOver = removeOnRunOver;
            this.inRoadPercentage = inRoadPercentage;
            pickupId = 0;
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

        obstacles.add(new ItemData("palmtree", false, 100, false, 0));
        obstacles.add(new ItemData("palmtree2", false, 100, false, 0));
        obstacles.add(new ItemData("palmtree3", false, 100, false, 0));
        obstacles.add(new ItemData("cone", false, 5, true, 0.9f));
        obstacles.add(new ItemData("manholecover", false, 0, false, 1));
        obstacles.add(new ItemData("barricade", false, 10, true, 0.9f));
        obstacles.add(new ItemData("barricade2", false, 10, true, 0.9f));
        obstacles.add(new ItemData("treeA", false, 100, false, 0));
        obstacles.add(new ItemData("treeC", false, 100, false, 0));
        obstacles.add(new ItemData("treeD", false, 100, false, 0));
        obstacles.add(new ItemData("treeE", false, 100, false, 0));
        obstacles.add(new ItemData("treeF", false, 100, false, 0));
        obstacles.add(new ItemData("treeG", false, 100, false, 0));
        obstacles.add(new ItemData("treeH", false, 100, false, 0));
        obstacles.add(new ItemData("cactusA", false, 20, true, 0));
        obstacles.add(new ItemData("cactusC", false, 20, true, 0));
        obstacles.add(new ItemData("cactusD", false, 20, true, 0));
        obstacles.add(new ItemData("cactusE", false, 20, true, 0));
        obstacles.add(new ItemData("cactusF", false, 20, true, 0));
        obstacles.add(new ItemData("cactusG", false, 20, true, 0));

        pickups.add(new ItemData("repairPickup", Repair, 0.8f));
        pickups.add(new ItemData("moneyPickup", Money, 0.8f));
        pickups.add(new ItemData("batteryPickup", Battery, 0.8f));
        pickups.add(new ItemData("weaponPickup", Weapon, 0.8f));
    }

    private ItemData item;

    public GameItem(GameScreen gameScreen, boolean pickup) {
        super(gameScreen);

        item = pickup ? pickups.get(MathUtils.random.nextInt(pickups.size)) : obstacles.get(MathUtils.random.nextInt(obstacles.size));
        setKeyFrame(item.image);
    }

    public static void AddItem(GameScreen gameScreen) {
        boolean pickup = MathUtils.random.nextFloat() > 0.97f;

        GameItem item = new GameItem(gameScreen, pickup);

        float x = 0;
        float y = gameScreen.camera.position.y + gameScreen.camera.viewportHeight;

        boolean inRoad = MathUtils.random.nextFloat() < item.item.inRoadPercentage;

        float left = gameScreen.road.getLeftEdge(y);
        float right = gameScreen.road.getRightEdge(y);

        if (inRoad) {
            x = left + ((right - left) *  MathUtils.random.nextFloat());
        } else {
            if (MathUtils.randomBoolean()) {
                x = left * MathUtils.random.nextFloat();
            } else {
                x = right + ((gameScreen.camera.viewportWidth - right) * MathUtils.random.nextFloat());
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
            car.health -= item.runoverDamage;
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
        switch (item.pickupId) {
            case Repair:
                car.health += 10;
                break;
            case Money:
                gameScreen.roundStats.moneyCollected += 10;
                break;
            case Battery:
                car.batteryLevel += 10;
                break;
            case Weapon:
                car.pickupAxe();
                break;
        }
    }
}

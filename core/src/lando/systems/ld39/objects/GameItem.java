package lando.systems.ld39.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld39.road.Road;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.utils.Assets;

/**
 * Created by Brian on 7/30/2017.
 */
public class GameItem extends GameObject {

    public static class ItemData {
        public TextureRegion image;
        public boolean isPickup;
        public boolean canRunover;
        public float inRoadPercentage;

        public ItemData(String textureImage, boolean isPickup, boolean canRunover, float inRoadPercentage) {
            image = Assets.atlas.findRegion(textureImage);
            this.isPickup = isPickup;
            this.canRunover = canRunover;
            this.inRoadPercentage = inRoadPercentage;
        }
    }

    private static Array<ItemData> obstacles = new Array<ItemData>();
    private static Array<ItemData> pickups = new Array<ItemData>();

    public static void load() {
        if (obstacles.size > 0) return;

        obstacles.add(new ItemData("palmtree", false, false, 0));
        obstacles.add(new ItemData("palmtree2", false, false, 0));
        obstacles.add(new ItemData("palmtree3", false, false, 0));
        obstacles.add(new ItemData("cone", false, false, 0.9f));
        obstacles.add(new ItemData("manholecover", false, true, 1));
        obstacles.add(new ItemData("barricade", false, false, 0.9f));
        obstacles.add(new ItemData("barricade2", false, false, 0.9f));
        obstacles.add(new ItemData("treeA", false, false, 0));
        obstacles.add(new ItemData("treeC", false, false, 0));
        obstacles.add(new ItemData("treeD", false, false, 0));
        obstacles.add(new ItemData("treeE", false, false, 0));
        obstacles.add(new ItemData("treeF", false, false, 0));
        obstacles.add(new ItemData("treeG", false, false, 0));
        obstacles.add(new ItemData("treeH", false, false, 0));
        obstacles.add(new ItemData("cactusA", false, false, 0));
        obstacles.add(new ItemData("cactusC", false, false, 0));
        obstacles.add(new ItemData("cactusD", false, false, 0));
        obstacles.add(new ItemData("cactusE", false, false, 0));
        obstacles.add(new ItemData("cactusF", false, false, 0));
        obstacles.add(new ItemData("cactusG", false, false, 0));

        pickups.add(new ItemData("repairPickup", true, true, 0.8f));
        pickups.add(new ItemData("moneyPickup", true, true, 0.8f));
        pickups.add(new ItemData("batteryPickup", true, true, 0.8f));
        pickups.add(new ItemData("weaponPickup", true, true, 0.8f));
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

        float bottomY =  (gameScreen.camera.position.y - gameScreen.camera.viewportHeight) / 2;
        remove = ((position.y + bounds_offset_y) < bottomY);
    }
}

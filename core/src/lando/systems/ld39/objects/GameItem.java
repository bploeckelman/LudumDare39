package lando.systems.ld39.objects;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld39.screens.GameScreen;
import lando.systems.ld39.ui.KilledBy;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.SoundManager;
import lando.systems.ld39.utils.accessors.RectangleAccessor;

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
        public String killedByName;

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

            if (textureImage.startsWith("palm")) killedByName = "Palm tree";
            else if (textureImage.startsWith("tree")) killedByName = "Tree";
            else if (textureImage.startsWith("cone")) killedByName = "Cone";
            else if (textureImage.startsWith("cactus")) killedByName = "Cactus";
            else killedByName = "Something?";
        }
    }

    private static Array<ItemData> obstacles = new Array<ItemData>();
    private static Array<ItemData> pickups = new Array<ItemData>();

    public static final int Repair = 0;
    public static final int Money = 1;
    public static final int Battery = 2;
    public static final int Weapon = 3;
    public static final int Cone = 4;

    public static void load() {
        if (obstacles.size > 0) return;

        obstacles.add(new ItemData("palmtree", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch, 33, 54));
        obstacles.add(new ItemData("palmtree2", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch, 25, 62));
        obstacles.add(new ItemData("palmtree3", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch, 33, 52));
        ItemData coneData = new ItemData("cone", false, 5, true, 0.8f, SoundManager.SoundOptions.crash_thump);
        coneData.pickupId = Cone;
        obstacles.add(coneData);
//        obstacles.add(new ItemData("manholecover", false, 0, false, 1, null));
//        obstacles.add(new ItemData("barricade", false, 10, true, 0.9f, null));
//        obstacles.add(new ItemData("barricade2", false, 10, true, 0.9f, null));
        obstacles.add(new ItemData("treeA", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch, 23, 42));
        obstacles.add(new ItemData("treeC", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch, 22, 37));
        obstacles.add(new ItemData("treeD", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch, 23, 42));
        obstacles.add(new ItemData("treeE", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("treeF", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("treeG", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("treeH", false, 50, false, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("cactusA", false, 20, true, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("cactusC", false, 20, true, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("cactusD", false, 20, true, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("cactusE", false, 20, true, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("cactusF", false, 20, true, 0, SoundManager.SoundOptions.cactus_crunch));
        obstacles.add(new ItemData("cactusG", false, 20, true, 0, SoundManager.SoundOptions.cactus_crunch));

        pickups.add(new ItemData("repairPickup", Repair, 0.6f, SoundManager.SoundOptions.pickup_repair));
        pickups.add(new ItemData("moneyPickup", Money, 0.6f, SoundManager.SoundOptions.pickup_money));
        pickups.add(new ItemData("batteryPickup", Battery, 0.6f, SoundManager.SoundOptions.pickup_battery));
        pickups.add(new ItemData("weaponPickup", Weapon, 0.6f, SoundManager.SoundOptions.pickup_weapon));
    }

    private ItemData item;
    private float animTime = 0;
    private boolean animate = false;

    public GameItem(GameScreen gameScreen, boolean pickup) {
        this(gameScreen, pickup, -1);
    }

    public GameItem(GameScreen gameScreen, boolean pickup, int pickupId) {
        super(gameScreen);

        if (!pickup) {
            item = obstacles.get(MathUtils.random.nextInt(obstacles.size));
        } else {
            if (pickupId == -1) {
                pickupId = MathUtils.random.nextInt(pickups.size);
            }
            item = pickups.get(pickupId);
            animTime = 1.0f;
            animate = true;
        }

        setKeyFrame(item.image);

        // todo - make bigger and remove this code.
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
        if (inRoad) {
            x = left + ((right - left) * MathUtils.random.nextFloat());
        } else {
            // move things that do more than 75% damage farther off road
            float padding = (item.item.inRoadPercentage == 0 && item.item.runoverDamage > (car.maxHealth * .75)) ? item.bounds_offset_x : 0;

            if (MathUtils.randomBoolean()) {
                x = (left - padding) * MathUtils.random.nextFloat();
            } else {
                x = right + padding;
                x += ((gameScreen.camera.viewportWidth - x) * MathUtils.random.nextFloat());
            }
        }

        item.setLocation(x, y);
        gameScreen.gameObjects.add(item);
    }

    float dist = 0;
    float pickupTime = 0f;
    boolean isPickingUp = false;
    Rectangle pickupBounds;
    float offsetY = 0;
    MutableFloat alpha = new MutableFloat(1f);

    @Override
    public void update(float dt) {
        super.update(dt);

        if (animate) {
            OrthographicCamera cam = gameScreen.camera;
            float topY = cam.position.y + cam.viewportHeight * 0.4f;

            if (animTime == 1f) {
                dist = topY - position.y;
            }
            animTime -= dt;
            if (animTime <= 0) {
                animate = false;
                animTime = 0;
            }

            float destX = gameScreen.road.getLeftEdge(topY);
            destX += (gameScreen.road.getRightEdge(topY) - destX) / 2;

            float curX = MathUtils.lerp(position.x, destX, 1 - animTime);
            float curY = topY - (dist * animTime);
            setLocation(curX, curY);
        }

        if (pickupTime > 0) {
            Rectangle newBounds = new Rectangle(pickupBounds);
            Assets.inflateRect(newBounds, 40 * (1 - pickupTime));
            bounds = newBounds;
            setY(gameScreen.camera.position.y - offsetY);
            if (item.pickupId != Cone) {
                alpha.setValue(alpha.floatValue() - dt);
            }

            pickupTime -= dt;
            if (pickupTime <= 0) {
                pickupTime = 0;
                remove = true;
            }
        }


        PlayerCar car = gameScreen.playerCar;
        if (car.collisionBounds.overlaps(collisionBounds) && !isPickingUp) {
            if (!item.isPickup && car.hasAxes()) {
                car.hitAxe();
                remove = true;
                if (item.pickupSoundType != null) {
                    SoundManager.playSound(item.pickupSoundType);
                }
            } else {
                car.addDamage(item.runoverDamage);
                if (item.pickupSoundType != null) {
                    SoundManager.playSound(item.pickupSoundType);
                }
                if (car.health <= 0) {
                    SoundManager.playSound(SoundManager.SoundOptions.crash_1);
                    gameScreen.killedBy = new KilledBy(item.killedByName, item.image, gameScreen.hudCamera);
                }
            }
            if (item.removeOnRunOver) {
                handlePickup(car, item);
            }
        } else {
            float bottomY = (gameScreen.camera.position.y - gameScreen.camera.viewportHeight) / 2;
            remove = ((position.y + bounds_offset_y) < bottomY);
        }
    }

    private void handlePickup(PlayerCar car, ItemData item) {
        // Special cases all day long
        if (!item.isPickup && item.pickupId != Cone) return;
        if (item.pickupSoundType != null) {
            SoundManager.playSound(item.pickupSoundType);
        }

        pickupTime = 1f;
        pickupBounds = new Rectangle(bounds);
        animate = false;
        offsetY = gameScreen.camera.position.y - position.y;
        isPickingUp = true;

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
            case Cone:
                Tween.to(alpha, 1, 0.5f).target(0).start(Assets.tween);
                break;
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        if (alpha.floatValue() <= 0) {
            alpha.setValue(0f);
        }

        batch.setColor(1, 1, 1, alpha.floatValue());
        super.render(batch);
        batch.setColor(Color.WHITE);
    }
}

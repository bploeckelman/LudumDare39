package lando.systems.ld39.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.IntIntMap;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.objects.*;
import lando.systems.ld39.particles.ParticleSystem;
import lando.systems.ld39.road.Road;
import lando.systems.ld39.ui.KilledBy;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;
import lando.systems.ld39.utils.Screenshake;
import lando.systems.ld39.utils.SoundManager;

/**
 * Created by Brian on 7/25/2017
 */
public class GameScreen extends BaseScreen {

    public static boolean DEBUG = false;

    public static float maxZoom = 1.6f;
    public static float minZoom = 0.2f;

    public static final Array<Bullet> activeBullets = new Array<Bullet>();
    public static final Pool<Bullet> bulletsPool = Pools.get(Bullet.class, 500);

    public Road road;

    public Array<GameObject> gameObjects = new Array<GameObject>();
    public Array<Vehicle> vehicles = new Array<Vehicle>();
    public ParticleSystem particleSystem = new ParticleSystem();

    public PlayerCar playerCar;

    public Rectangle constraintBounds;
    private Vector2 constraintOffset;
    public Rectangle viewBounds;

    public boolean bossActive;
    public boolean killedMiniBoss;
    public boolean killedMusk;
    public boolean pause;

    public Stats roundStats;

    public KilledBy killedBy;
    public Screenshake shaker;

    public GameScreen() {
        PlayerCar tempPlayerCar = new PlayerCar(this);
        init(tempPlayerCar.getUpgrades());
    }

    public GameScreen(IntIntMap currentUpgrades) {
        init(currentUpgrades);
    }

    public void init(IntIntMap currentUpgrades) {
        roundStats = new Stats();
        alpha.setValue(1);
        road = new Road();

        killedBy = null;
        shaker = new Screenshake(120, 3);

        constraintBounds = new Rectangle(0, 10, camera.viewportWidth, camera.viewportHeight * 0.7f);
        constraintOffset = new Vector2((camera.viewportWidth /2) - 10, camera.viewportHeight /2);
        pause = true;
        bossActive = false;
        killedMiniBoss = false;
        killedMusk = false;
        viewBounds = new Rectangle(0,0,camera.viewportWidth, camera.viewportHeight);
        createCar(currentUpgrades);
        Tween.to(alpha, 1, 1)
                .target(0)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        pause = false;

                    }
                })
                .start(Assets.tween);

        Gdx.input.setInputProcessor(this);
    }

    private void createCar(IntIntMap currentUpgrades) {
        playerCar = new PlayerCar(this);
        playerCar.constraintBounds = constraintBounds;
        playerCar.setUpgrades(currentUpgrades);
        playerCar.setStatsBasedOnUpgradeLevels();
        vehicles.add(playerCar);
    }

    // temp enemy gen
    private float hammerTime = 7;

    private void addEnemy(float dt) {
        if (bossActive) return;
        if (vehicles.size > 3) return;
        hammerTime += dt;
        if (hammerTime > 2) {
            hammerTime = 0;
            vehicles.add(EnemyCar.getEnemy(this));
        }
    }

    public Array<GameItem> drops = new Array<GameItem>(5);

    float addCounter = 0;
    float addTime = 0;
    @Override
    public void update(float dt) {
//        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
//            Gdx.app.exit();
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
//            LudumDare39.game.setScreen(new UpgradeScreen(playerCar.getUpgrades()));
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
//            shaker.shake(2f);
//        }

        particleSystem.update(dt);

        if (playerCar.dead && killedBy == null) {
            killedBy = new KilledBy("Too much damage", Assets.healthTexture, hudCamera);
        }

        if ((pause && killedBy != null)) {
            if (Gdx.input.justTouched()) {
                Tween.to(alpha, 1, 1)
                        .target(1)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                killedBy = null;
                                LudumDare39.game.setScreen(new MapScreen(roundStats, playerCar));
                            }
                        })
                        .start(Assets.tween);
            }
        }
        if (pause) return;

        addItems(dt);

        addEnemy(dt);
        updateWorld(dt);
        updateObjects(dt);
        updateCamera(dt);

        shaker.update(dt, camera, camera.position.x, camera.position.y);
    }

    private void addItems(float dt) {
        addCounter += dt;
        if (addCounter > addTime) {
            GameItem.AddItem(this);
            addCounter = 0;
            addTime = MathUtils.random.nextFloat() * 0.4f;
        }

        for (GameItem gi : drops) {
            gameObjects.add(gi);
        }
        drops.clear();
    }

    private void updateWorld(float dt) {
        road.update(dt);
    }

    private void updateObjects(float dt) {
        viewBounds.y = camera.position.y - camera.viewportHeight/2f;
        for(int i = activeBullets.size - 1; i >= 0; i--){
            Bullet b = activeBullets.get(i);
            b.update(dt);
            for (Vehicle car : vehicles){
                if (car != b.owner && car.bounds.contains(b.position) && !car.dead && !car.invincible) {
                    if (b.owner instanceof EnemyCar && car instanceof EnemyCar) continue;

                    car.addDamage(b.damage);
                    b.alive = false;
                    break;
                }
            }

            if (!viewBounds.contains(b.position)){ b.alive = false; }
            if (!b.alive){
                activeBullets.removeIndex(i);
                bulletsPool.free(b);
            }
        }
        playerCar.constraintBounds = constraintBounds;
        for (int i = gameObjects.size -1; i >= 0; i--){
            GameObject o = gameObjects.get(i);
            o.update(dt);
            if (o.remove) {
                gameObjects.removeIndex(i);
            }
        }
        for (int i = vehicles.size -1; i >= 0; i--){
            Vehicle v = vehicles.get(i);
            v.update(dt);
            if (v.remove) {
                vehicles.removeIndex(i);
            }
        }
        if (playerCar.dead){
            SoundManager.soundMap.get(SoundManager.SoundOptions.coast).stop();
            shaker.shake(2f);

            // TODO make this move to MapScreen
            // Set the distanceTraveledPercent
            roundStats.distanceTraveledPercent = (road.distanceTraveled)/road.endRoad;
            pause = true;
            removeAllBullets();
        }
    }

    private void updateCamera(float dt) {
        camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom);

        float deltaY = playerCar.getSpeed() * dt;

        // move camera based on car speed - update position of car in so it doesn't drop
        playerCar.setLocation(playerCar.position.x, playerCar.position.y + deltaY);
//        playerCar.position.y += deltaY;
        camera.position.y += deltaY;
        constraintBounds.y += deltaY;
        if (!bossActive) {
            road.distanceTraveled += ( deltaY / road.segmentLength);
        }
        if (!killedMiniBoss){
            if (!bossActive && road.distanceTraveled >= road.endRoad/2f){
                bossActive = true;
                vehicles.add(EnemyCar.getMiniBoss(this));
            }
            road.distanceTraveled = Math.min(road.distanceTraveled, road.endRoad/2f);
        }
        if (!bossActive && road.distanceTraveled >= road.endRoad){
            bossActive = true;
            road.clearRoad(camera.position.y);
            vehicles.add(EnemyCar.getMusk(this));
        }

        road.distanceTraveled = Math.min(road.distanceTraveled, road.endRoad);

        camera.update();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw world
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            road.renderFrameBuffer(batch, camera);
            renderWorld(batch);
            particleSystem.render(batch);
            renderObjects(batch);
            for (Bullet b : activeBullets){
                b.render(batch);
            }
        }
        batch.end();

        // Draw hud
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            renderHud(batch);
            batch.setColor(0, 0, 0, alpha.floatValue());
            batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.setColor(Color.WHITE);

            if (DEBUG) {
                Assets.drawString(batch, "Now Battery: " + (int) playerCar.batteryLevel, 10f, 20f, Color.WHITE, 0.3f, Assets.font);
                Assets.drawString(batch, "Max Battery: " + (int) playerCar.maxBattery, 10f, 40f, Color.WHITE, 0.3f, Assets.font);

                Assets.drawString(batch, "Now Health : " + (int) playerCar.health, 10f, 70f, Color.WHITE, 0.3f, Assets.font);
                Assets.drawString(batch, "Max Health : " + (int) playerCar.maxHealth, 10f, 90f, Color.WHITE, 0.3f, Assets.font);

                Assets.drawString(batch, "Now Speed  : " + (int) playerCar.speed, 10f, 120f, Color.WHITE, 0.3f, Assets.font);
                Assets.drawString(batch, "Max Speed  : " + (int) playerCar.maxSpeed, 10f, 140f, Color.WHITE, 0.3f, Assets.font);
            }
        }
        batch.end();
    }

    private void renderWorld(SpriteBatch batch) {
        road.render(batch, camera);
    }

    private void renderObjects(SpriteBatch batch) {
        // car is first element, make sure it's on top
        for (int i = vehicles.size - 1; i >= 0; i--) {
            vehicles.get(i).render(batch);
        }
        for (int i = gameObjects.size - 1; i >= 0; i--) {
            gameObjects.get(i).render(batch);
        }
    }

    Color drawColor = new Color();
    private void renderHud(SpriteBatch batch) {
        batch.setShader(Assets.hudShader);
        batch.setColor(Color.YELLOW);
        float percent = playerCar.getBatteryPercent();
        Assets.hsvToRgb(percent * 120/ 365f, 1f, .8f, drawColor);
        Assets.hudShader.setUniformf("amount", percent);
        Assets.hudShader.setUniformf("fillColor", drawColor);
        batch.draw(Assets.lightningTexture, 0, camera.viewportHeight /2 - 50, 100, 100);
        batch.flush();
        batch.setColor(Color.GREEN);
        float healthPercent = playerCar.getHealthPercent();
        Assets.hsvToRgb(MathUtils.clamp(healthPercent * 120/ 365f, 0f, 1f), 1f, .8f, drawColor);
        Assets.hudShader.setUniformf("amount", healthPercent);
        Assets.hudShader.setUniformf("fillColor", drawColor);
        batch.draw(Assets.healthTexture, camera.viewportWidth - 80, camera.viewportHeight / 2 - 30, 60, 60);

        batch.setShader(null);
        String text = (int)(percent * 100) + "%";
        Assets.drawString(batch, text, 10, camera.viewportHeight / 2 + 70, Color.WHITE, .35f, Assets.font, 100, Align.center);
//            hud.render(batch);

        if (killedBy != null) {
            batch.setColor(0f, 0f, 0f, 0.7f);
            batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.setColor(Color.WHITE);
            killedBy.render(batch);
        }
    }

    public void removeAllBullets(){
        bulletsPool.freeAll(activeBullets);
        activeBullets.clear();
    }

    public void addBullet(Vehicle owner, Vector2 velocity, Vector2 position, TextureRegion tex, float damage){
        Bullet b = bulletsPool.obtain();
        b.init(position, velocity, owner, tex, damage);
        activeBullets.add(b);
    }


    int[] sequence = new int [] { Input.Keys.UP, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.B, Input.Keys.A};
    int index = 0;
    public boolean keyUp(int keyCode) {
        if (index >= sequence.length) index = 0;
        if (sequence[index] == keyCode) {
            if (++index == sequence.length) {
                SoundManager.playSound(SoundManager.SoundOptions.cash_money);
                roundStats.moneyCollected += 10000;
                index = 0;
            }
        } else {
            index = 0;
        }
        return false;
    }
}

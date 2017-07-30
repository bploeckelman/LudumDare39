package lando.systems.ld39.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.objects.*;
import lando.systems.ld39.particles.ParticleSystem;
import lando.systems.ld39.road.Road;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

/**
 * Created by Brian on 7/25/2017
 */
public class GameScreen extends BaseScreen {

    public static float zoomScale = 0.15f;
    public static float maxZoom = 1.6f;
    public static float minZoom = 0.2f;
    public static float DRAG_DELTA = 10f;

    public static final Array<Bullet> activeBullets = new Array<Bullet>();
    public static final Pool<Bullet> bulletsPool = Pools.get(Bullet.class, 500);

    public Road road;

    public Array<GameObject> gameObjects = new Array<GameObject>();
    public ParticleSystem particleSystem = new ParticleSystem();

    public PlayerCar playerCar;

    public Rectangle constraintBounds;
    private Vector2 constraintOffset;
    public Rectangle viewBounds;

    private Vector3 touchStart;
    private Vector3 cameraTouchStart;
    private boolean cancelTouchUp;
    public boolean bossActive;
    public boolean pause;

    public GameScreen() {
        alpha.setValue(1);
        road = new Road();
        touchStart = new Vector3();
        cameraTouchStart = new Vector3();
        cancelTouchUp = false;

        constraintBounds = new Rectangle(0, 10, camera.viewportWidth, camera.viewportHeight * 0.7f);
        constraintOffset = new Vector2((camera.viewportWidth /2) - 10, camera.viewportHeight /2);
        pause = true;
        bossActive = false;
        viewBounds = new Rectangle(0,0,camera.viewportWidth, camera.viewportHeight);
        createCar();
        Tween.to(alpha, 1, 1)
                .target(0)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        pause = false;

                    }
                })
                .start(Assets.tween);
    }

    private void createCar() {

        playerCar = new PlayerCar(this);
        playerCar.constraintBounds = constraintBounds;
        gameObjects.add(playerCar);
    }

    // temp enemy gen
    private float hammerTime = 7;

    private void addEnemy(float dt) {
        hammerTime += dt;
        if (hammerTime > 5) {
            hammerTime = 0;
            gameObjects.add(EnemyCar.getEnemy(this));
        }
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            LudumDare39.game.setScreen(new UpgradeScreen());
        }

        particleSystem.update(dt);

        if (pause) return;

        addEnemy(dt);
        updateWorld(dt);
        updateObjects(dt);
        updateCamera(dt);
    }

    private void updateWorld(float dt) {
        road.update(dt);
    }

    private void updateObjects(float dt) {
        viewBounds.y = camera.position.y - camera.viewportHeight/2f;
        for(int i = activeBullets.size - 1; i >= 0; i--){
            Bullet b = activeBullets.get(i);
            b.update(dt);
            for (GameObject car : gameObjects){
                if (!(car instanceof Vehicle)) continue;
                if (car != b.owner && car.bounds.contains(b.position)) {
                    ((Vehicle) car).health -= b.damage;
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
        for(GameObject gameObject : gameObjects) {
            gameObject.update(dt);
        }
        if (playerCar.dead){
            // TODO make this move to MapScreen
            pause = true;
            removeAllBullets();
            Tween.to(alpha, 1, 1)
                    .target(1)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            LudumDare39.game.setScreen(new MapScreen((road.distanceTraveled)/road.endRoad));

                        }
                    })
                    .start(Assets.tween);
        }
    }

    private void updateCamera(float dt) {
        camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom);

        float deltaY = playerCar.speed * dt;
        // move camera based on car speed - update position of car in so it doesn't drop
        playerCar.position.y += deltaY;
        camera.position.y += deltaY;
        constraintBounds.y += deltaY;
        if (!bossActive) {
            road.distanceTraveled += ( deltaY / road.segmentLength);
        }

        // Keep camera within world bounds
//        float minY = world.bounds.y + camera.viewportHeight / 2 * camera.zoom;
//        float maxY = world.bounds.height - camera.viewportHeight / 2 * camera.zoom;
//        float minX = world.bounds.x + camera.viewportWidth / 2 * camera.zoom;
//        float maxX = world.bounds.x + world.bounds.width - camera.viewportWidth / 2 * camera.zoom;
//        if (camera.viewportHeight * camera.zoom > world.bounds.height) {
//            camera.position.y = world.bounds.height / 2;
//        } else {
//            camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
//        }
//        if (camera.viewportWidth * camera.zoom > world.bounds.width) {
//            camera.position.x = world.bounds.x + world.bounds.width / 2;
//        } else {
//            camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
//        }

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
        }
        batch.end();
    }

    private void renderWorld(SpriteBatch batch) {
        road.render(batch, camera);
    }

    private void renderObjects(SpriteBatch batch) {
        for (GameObject gameObject : gameObjects) {
            gameObject.render(batch);
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
        Assets.hsvToRgb(healthPercent * 120/ 365f, 1f, .8f, drawColor);
        Assets.hudShader.setUniformf("amount", healthPercent);
        Assets.hudShader.setUniformf("fillColor", drawColor);
        batch.draw(Assets.healthTexture, camera.viewportWidth - 80, camera.viewportHeight /2 - 30, 60, 60);

        batch.setShader(null);
        String text = (int)(percent * 100) + "%";
        Assets.drawString(batch, text, 10, camera.viewportHeight/2 + 70,Color.WHITE, .35f, Assets.font, 100, Align.center);
//            hud.render(batch);
    }

    // ------------------------------------------------------------------------
    // InputAdapter Overrides
    // ------------------------------------------------------------------------

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        cameraTouchStart.set(camera.position);
        touchStart.set(screenX, screenY, 0);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        camera.position.x = cameraTouchStart.x + (touchStart.x - screenX) * camera.zoom;
        camera.position.y = cameraTouchStart.y + (screenY - touchStart.y) * camera.zoom;
        if (cameraTouchStart.dst(camera.position) > DRAG_DELTA) {
            cancelTouchUp = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (cancelTouchUp) {
            cancelTouchUp = false;
            return false;
        }

        return false;
    }

    private Vector3 currentUnprojectedTouch = new Vector3();
    @Override
    public boolean scrolled(int change) {
        camera.unproject(currentUnprojectedTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        camera.zoom += change * camera.zoom * zoomScale;

        updateCamera(Gdx.graphics.getDeltaTime());

        float prevUnprojectedTouchX = currentUnprojectedTouch.x;
        float prevUnprojectedTouchY = currentUnprojectedTouch.y;
        camera.unproject(currentUnprojectedTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        camera.position.add(prevUnprojectedTouchX - currentUnprojectedTouch.x, prevUnprojectedTouchY - currentUnprojectedTouch.y, 0);
        camera.update();

        return true;
    }

    public void removeAllBullets(){
        bulletsPool.freeAll(activeBullets);
        activeBullets.clear();
    }

    public void addBullet(Vehicle owner, Vector2 velocity){
        Bullet b = bulletsPool.obtain();
        b.init(owner.position, velocity, owner);
        activeBullets.add(b);
    }
}

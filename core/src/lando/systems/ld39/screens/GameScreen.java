package lando.systems.ld39.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;
import lando.systems.ld39.utils.accessors.RectangleAccessor;

/**
 * Created by Brian on 7/25/2017
 */
public class GameScreen extends BaseScreen {

    public static float zoomScale = 0.15f;
    public static float maxZoom = 1.6f;
    public static float minZoom = 0.2f;
    public static float DRAG_DELTA = 10f;

    private Vector3 touchStart;
    private Vector3 cameraTouchStart;
    private boolean cancelTouchUp;

    private Rectangle testPixel;
    private Vector2 testPixelVel;

    public GameScreen() {
        touchStart = new Vector3();
        cameraTouchStart = new Vector3();
        cancelTouchUp = false;

        testPixel = new Rectangle(camera.viewportWidth / 2f - 5f,
                                  camera.viewportHeight / 2f - 5f,
                                  20f, 20f);
        testPixelVel = new Vector2(120f, 120f);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        updateWorld(dt);
        updateCamera(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw world
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            // world.render(batch);
            batch.draw(Assets.whitePixel, testPixel.x, testPixel.y, testPixel.width, testPixel.height);
        }
        batch.end();

        // Draw hud
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
//            hud.render(batch);
        }
        batch.end();
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

    // ------------------------------------------------------------------------
    // Utility Methods
    // ------------------------------------------------------------------------

    private void updateWorld(float dt) {
        testPixel.x += testPixelVel.x * dt;
        testPixel.y += testPixelVel.y * dt;

        if (testPixel.x + testPixel.width >= camera.viewportWidth) {
            testPixel.x = camera.viewportWidth - testPixel.width;
            testPixelVel.x *= -1f;
            startTestPixelSizeBounce();
        } else if (testPixel.x <= 0f) {
            testPixel.x = 0f;
            testPixelVel.x *= -1f;
            startTestPixelSizeBounce();
        }

        if (testPixel.y + testPixel.height >= camera.viewportHeight) {
            testPixel.y = camera.viewportHeight - testPixel.height;
            testPixelVel.y *= -1f;
            startTestPixelSizeBounce();
        } else if (testPixel.y <= 0f) {
            testPixel.y = 0f;
            testPixelVel.y *= -1f;
            startTestPixelSizeBounce();
        }
    }

    private void startTestPixelSizeBounce() {
        Tween.to(testPixel, RectangleAccessor.WH, 0.05f)
                .target(10f, 10f)
                .repeatYoyo(1, 0f)
                .start(Assets.tween);
    }

    private void updateCamera(float dt) {
        camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom);

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

}

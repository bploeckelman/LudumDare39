package lando.systems.ld39.screens;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.objects.Map;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class MapScreen extends BaseScreen {

    private static final float TIME_MAP_FADE_IN = 1;
    private static final float TIME_DRAW_ROUTE_TRAVELED = 3;

    private static final Color ROUTE_TRAVELED_COLOR = Color.BLUE;
    private static final Color ROUTE_UNTRAVELED_COLOR = Color.WHITE;
    private static final float ROUTE_WIDTH = 4;

    private static final float DISP_ROUTE_KM = 4174;

    private Vector2[] routePoints;
    private CatmullRomSpline<Vector2> routeSpline;

    private MutableFloat animationPercent = new MutableFloat(0);
    private MutableFloat mapAlpha = new MutableFloat(0);
    private Stage currentStage;

    private float distanceTraveled = 0;

    private enum Stage {
        MAP_FADE_IN,
        ANIMATE_TRAVEL
    }


    /**
     *
     * @param distanceTraveled value between 0 and 1, inclusive.
     */
    public MapScreen(float distanceTraveled) {

        this.distanceTraveled = distanceTraveled;
        currentStage = Stage.MAP_FADE_IN;

        routeSpline = new CatmullRomSpline<Vector2>(Map.ROUTE_POINTS, false);
        routePoints = new Vector2[Map.ROUTE_RASTER_COUNT];
        for (int i = 0; i < Map.ROUTE_RASTER_COUNT; i++) {
            routePoints[i] = new Vector2();
            routeSpline.valueAt(routePoints[i], ((float)i) / ((float) Map.ROUTE_RASTER_COUNT-1));
        }

        TweenCallback onMapFadeInComplete = new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> baseTween) {
                currentStage = Stage.ANIMATE_TRAVEL;
            }
        };

        Timeline.createSequence()
                .push(Tween.to(mapAlpha, 1, TIME_MAP_FADE_IN)
                        .ease(TweenEquations.easeOutSine)
                        .target(1))
                .push(Tween.call(onMapFadeInComplete))
                .push(Tween.to(animationPercent, 1, TIME_DRAW_ROUTE_TRAVELED)
                        .ease(TweenEquations.easeInOutQuad)
                        .target(1))
                .start(Assets.tween);

    }

    @Override
    public void update(float dt) {
//        current += dt;
        if (Gdx.input.justTouched()) {
            // TODO: speed up the route animation
        }
    }

    @Override
    public void render(SpriteBatch batch) {

        float splineDistance = distanceTraveled * animationPercent.floatValue();

        // Draw the Map
        batch.begin();
        batch.draw(Assets.map, 0, 0, Map.MAP_DRAW_WIDTH, Map.MAP_DRAW_HEIGHT);
        batch.end();

        // Draw the route.
        Assets.shapes.begin(ShapeRenderer.ShapeType.Filled);
        Color thisRouteColor = ROUTE_TRAVELED_COLOR;
        Vector2 lastTraveledRasterPoint = null;
        float nextRasterPercent;
        for(int i = 0; i < Map.ROUTE_RASTER_COUNT-1; ++i) {
            nextRasterPercent = ((float) i+1) / ((float) Map.ROUTE_RASTER_COUNT-1);
            if (lastTraveledRasterPoint == null && nextRasterPercent >= splineDistance) {
                thisRouteColor = ROUTE_UNTRAVELED_COLOR;
                lastTraveledRasterPoint = routePoints[i];
            }

            Assets.shapes.setColor(thisRouteColor);
            Assets.shapes.rectLine(
                    routePoints[i].x, routePoints[i].y,
                    routePoints[i+1].x, routePoints[i+1].y,
                    4,
                    thisRouteColor, thisRouteColor);
            Assets.shapes.circle(routePoints[i].x, routePoints[i].y, ROUTE_WIDTH/2);
        }

        // Player location
        Vector2 loc = new Vector2();
        routeSpline.valueAt(loc, splineDistance);
        if (lastTraveledRasterPoint != null) {
            // Draw a line from the last traveled point to the player location
            Assets.shapes.setColor(ROUTE_TRAVELED_COLOR);
            Assets.shapes.rectLine(lastTraveledRasterPoint.x, lastTraveledRasterPoint.y, loc.x, loc.y, ROUTE_WIDTH);
            Assets.shapes.circle(lastTraveledRasterPoint.x, lastTraveledRasterPoint.y, ROUTE_WIDTH/2);
        }
        // Draw the player
        Assets.shapes.setColor(Color.WHITE);
        Assets.shapes.circle(loc.x, loc.y, 8);
        Assets.shapes.setColor(Color.RED);
        Assets.shapes.circle(loc.x, loc.y, 6);
        Assets.shapes.end();

        if (currentStage == Stage.MAP_FADE_IN) {
            // On top of everything, "fade in" the map by drawing black on top of it.
            batch.begin();
            batch.setColor(0,0,0,(1 - mapAlpha.floatValue()));
            batch.draw(Assets.whitePixel, 0, 0, Config.gameWidth, Config.gameHeight);
            batch.setColor(Color.WHITE);
            batch.end();
        }

    }

}

package lando.systems.ld39.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.objects.Map;
import lando.systems.ld39.utils.Assets;

public class MapScreen extends BaseScreen {

    private static final float ROUTE_ANIMATION_TIME = 3;
    private static final Color ROUTE_TRAVELED_COLOR = Color.RED;
    private static final Color ROUTE_UNTRAVELED_COLOR = Color.WHITE;
    private static final float ROUTE_WIDTH = 4;

    private Vector2[] routePoints;
    private CatmullRomSpline<Vector2> routeSpline;

    private MutableFloat animationPercent = new MutableFloat(0);

    private float distanceTraveled = 0;


    /**
     *
     * @param distanceTraveled value between 0 and 1, inclusive.
     */
    public MapScreen(float distanceTraveled) {

        this.distanceTraveled = distanceTraveled;

        routeSpline = new CatmullRomSpline<Vector2>(Map.ROUTE_POINTS, false);
        routePoints = new Vector2[Map.ROUTE_RASTER_COUNT];
        for (int i = 0; i < Map.ROUTE_RASTER_COUNT; i++) {
            routePoints[i] = new Vector2();
            routeSpline.valueAt(routePoints[i], ((float)i) / ((float) Map.ROUTE_RASTER_COUNT-1));
        }

        Timeline.createSequence()
                .push(Tween.to(animationPercent, 1, ROUTE_ANIMATION_TIME)
                        .ease(TweenEquations.easeInOutQuad)
                    .target(1))
                .start(Assets.tween);

    }

    @Override
    public void update(float dt) {
//        current += dt;
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
            if (nextRasterPercent >= splineDistance) {
                thisRouteColor = ROUTE_UNTRAVELED_COLOR;
                if (lastTraveledRasterPoint == null) {
                    lastTraveledRasterPoint = routePoints[i];
                }
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
        Assets.shapes.setColor(Color.BLACK);
        Assets.shapes.circle(loc.x, loc.y, 4);
        Assets.shapes.end();

    }

}

package lando.systems.ld39.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.objects.Map;
import lando.systems.ld39.utils.Assets;

public class MapScreen extends BaseScreen {

    private static final float ROUTE_ANIMATION_TIME = 3;

    private Vector2[] routePoints;
    private CatmullRomSpline<Vector2> routeSpline;
    private float current = 0;

    public MapScreen() {

        routeSpline = new CatmullRomSpline<Vector2>(Map.ROUTE_POINTS, false);
        routePoints = new Vector2[Map.ROUTE_RASTER_COUNT];
        for (int i = 0; i < Map.ROUTE_RASTER_COUNT; i++) {
            routePoints[i] = new Vector2();
            routeSpline.valueAt(routePoints[i], ((float)i) / ((float) Map.ROUTE_RASTER_COUNT-1));
        }

    }



    @Override
    public void update(float dt) {
        current += dt;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(Assets.map, 0, 0, Map.MAP_DRAW_WIDTH, Map.MAP_DRAW_HEIGHT);
        batch.end();
        Assets.shapes.begin(ShapeRenderer.ShapeType.Filled);
        Assets.shapes.setColor(Color.RED);
        for(int i = 0; i < Map.ROUTE_RASTER_COUNT-1; ++i) {
            Assets.shapes.rectLine(routePoints[i].x, routePoints[i].y,
                    routePoints[i+1].x, routePoints[i+1].y,
                    4,Color.RED,Color.RED);
            Assets.shapes.circle(routePoints[i].x, routePoints[i].y, 2);
        }

        Vector2 loc = new Vector2();
//        if (current >= ROUTE_ANIMATION_TIME) {
//            routeSpline.valueAt(loc, 1);
//        } else {
//
//        }

        routeSpline.valueAt(loc, (current % ROUTE_ANIMATION_TIME) / ROUTE_ANIMATION_TIME);
//        routeSpline.valueAt(loc, (current % ROUTE_ANIMATION_TIME) / ROUTE_ANIMATION_TIME);
        Assets.shapes.setColor(Color.BLACK);
        Assets.shapes.circle(loc.x, loc.y, 4);
        Assets.shapes.end();

    }



}

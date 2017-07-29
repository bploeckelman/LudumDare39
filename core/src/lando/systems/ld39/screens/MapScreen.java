package lando.systems.ld39.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

import java.util.LinkedList;

public class MapScreen extends BaseScreen {

    private final int ROUTE_RASTER_COUNT = 64;
    private final int MAP_IMAGE_WIDTH = 640;
    private final int MAP_IMAGE_HEIGHT = 480;
    private final int MAP_DRAW_WIDTH = Config.gameWidth;
    private final int MAP_DRAW_HEIGHT = Config.gameHeight;

    private Vector2[] routePoints;
    private CatmullRomSpline<Vector2> routeSpline;

    /**
     *
     * @param x
     * @param y The y coordinate of the point on the original image measured from the TOP (will be inverted)
     * @return
     */
    private Vector2 getScaledMapVector(int x, int y) {
        int modifiedX = Math.round(((float) x) / ((float) MAP_IMAGE_WIDTH) * ((float) MAP_DRAW_WIDTH));
        int modifiedY = Math.round(((float) (MAP_IMAGE_HEIGHT - y)) / ((float) MAP_IMAGE_HEIGHT) * ((float) MAP_DRAW_HEIGHT));
        return new Vector2(modifiedX, modifiedY);
    }

    public MapScreen() {


        LinkedList<Vector2> linkedRouteVectorSet = new LinkedList<Vector2>();
        linkedRouteVectorSet.add( getScaledMapVector(540, 216));
        linkedRouteVectorSet.add( getScaledMapVector(540, 216));
        linkedRouteVectorSet.add( getScaledMapVector(520, 196));
        linkedRouteVectorSet.add( getScaledMapVector(504, 197));
        linkedRouteVectorSet.add( getScaledMapVector(483, 185));
        linkedRouteVectorSet.add( getScaledMapVector(473, 189));
        linkedRouteVectorSet.add( getScaledMapVector(463, 184));
        linkedRouteVectorSet.add( getScaledMapVector(419, 192));
        linkedRouteVectorSet.add( getScaledMapVector(399, 201));
        linkedRouteVectorSet.add( getScaledMapVector(360, 193));
        linkedRouteVectorSet.add( getScaledMapVector(328, 201));
        linkedRouteVectorSet.add( getScaledMapVector(319, 210));
        linkedRouteVectorSet.add( getScaledMapVector(283, 209));
        linkedRouteVectorSet.add( getScaledMapVector(272, 200));
        linkedRouteVectorSet.add( getScaledMapVector(259, 200));
        linkedRouteVectorSet.add( getScaledMapVector(235, 195));
//        linkedRouteVectorSet.add( getScaledMapVector(235, 195));
        linkedRouteVectorSet.add( getScaledMapVector(223, 197));
        linkedRouteVectorSet.add( getScaledMapVector(206, 185));
        linkedRouteVectorSet.add( getScaledMapVector(162, 189));
        linkedRouteVectorSet.add( getScaledMapVector(146, 201));
        linkedRouteVectorSet.add( getScaledMapVector(140, 196));
        linkedRouteVectorSet.add( getScaledMapVector(128, 196));
        linkedRouteVectorSet.add( getScaledMapVector(117, 184));
        linkedRouteVectorSet.add( getScaledMapVector( 99, 187));
        linkedRouteVectorSet.add( getScaledMapVector( 85, 178));
        linkedRouteVectorSet.add( getScaledMapVector( 70, 198));
        linkedRouteVectorSet.add( getScaledMapVector( 65, 198));
        linkedRouteVectorSet.add( getScaledMapVector( 65, 198));

        Vector2[] routeDataSet = new Vector2[linkedRouteVectorSet.size()];
        for (int i = 0; i < linkedRouteVectorSet.size(); i++) {
            routeDataSet[i] = linkedRouteVectorSet.get(i);  
        }
        
        
        
        routeSpline = new CatmullRomSpline<Vector2>(routeDataSet, false);
        routePoints = new Vector2[ROUTE_RASTER_COUNT];
        for (int i = 0; i < ROUTE_RASTER_COUNT; i++) {
            routePoints[i] = new Vector2();
            routeSpline.valueAt(routePoints[i], ((float)i) / ((float)ROUTE_RASTER_COUNT-1));
        }
//        Vector2 out = new Vector2();
//        myCatmull.valueAt(out, t);
//        myCatmull.derivativeAt(out, t);
    }

    private float routeCurrent = 0;
    private float current = 0;
    private final float ROUTE_SPEED = 1;

    @Override
    public void update(float dt) {
        current += dt;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(Assets.map, 0, 0, MAP_DRAW_WIDTH, MAP_DRAW_HEIGHT);
        batch.end();
        Assets.shapes.begin(ShapeRenderer.ShapeType.Filled);
        Assets.shapes.setColor(Color.RED);
        for(int i = 0; i < ROUTE_RASTER_COUNT-1; ++i) {
//            Assets.shapes.line(routePoints[i], routePoints[i+1]);
            Assets.shapes.rectLine(routePoints[i].x, routePoints[i].y,
                    routePoints[i+1].x, routePoints[i+1].y,
                    4,Color.RED,Color.RED);
            Assets.shapes.circle(routePoints[i].x, routePoints[i].y, 2);
        }

//        Vector2 dLoc = new Vector2();
//        routeSpline.derivativeAt(dLoc, current % 1);
//        routeCurrent += (Gdx.graphics.getDeltaTime() * ROUTE_SPEED) / dLoc.len();
//        routeCurrent %= 1;
        Vector2 loc = new Vector2();
        routeSpline.valueAt(loc, (current % 10) / 10);
        Assets.shapes.setColor(Color.BLACK);
        Assets.shapes.circle(loc.x, loc.y, 4);
        Assets.shapes.end();

    }

}

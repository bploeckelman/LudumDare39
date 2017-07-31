package lando.systems.ld39.screens;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.objects.Map;
import lando.systems.ld39.objects.PlayerCar;
import lando.systems.ld39.objects.Stats;
import lando.systems.ld39.ui.MapScreenHud;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

import static lando.systems.ld39.screens.MapScreen.Stage.ANIMATE_TRAVEL;
import static lando.systems.ld39.screens.MapScreen.Stage.FADE_OUT;
import static lando.systems.ld39.screens.MapScreen.Stage.HUD_CONTROL;

public class MapScreen extends BaseScreen {

    private static final float TIME_PAUSE = 0.5f;
    private static final float TIME_MAP_FADE_IN = 1;
    private static final float TIME_MAP_FADE_OUT = 1;
    private static final float TIME_DRAW_ROUTE_TRAVELED = 3;

    private static final Color ROUTE_TRAVELED_COLOR = Color.BLUE;
    private static final Color ROUTE_UNTRAVELED_COLOR = Color.WHITE;
    private static final float ROUTE_WIDTH = 4;

    public static final float DISP_ROUTE_KM = 4174f;

    private Vector2[] routePoints;
    private CatmullRomSpline<Vector2> routeSpline;

    private MutableFloat animationPercent = new MutableFloat(0);
    private MutableFloat alpha = new MutableFloat(0);
    private Stage currentStage;
    private float currentStagePercent = 0;
    private float currentStageTime = 0;
    private MapScreenHud mapScreenHud;

    private final Stats roundStats;
    private final PlayerCar playerCar;

    public enum Stage {
        FADE_IN,
        ANIMATE_TRAVEL,
        HUD_CONTROL,
        HUD_COMPLETE,
        FADE_OUT
    }


    /**
     *
     * @param roundStats
     */
    public MapScreen(Stats roundStats, PlayerCar playerCar) {

        this.roundStats = roundStats;
        this.playerCar = playerCar;
        currentStage = Stage.FADE_IN;
        mapScreenHud = new MapScreenHud(this, roundStats);

        routeSpline = new CatmullRomSpline<Vector2>(Map.ROUTE_POINTS, false);
        routePoints = new Vector2[Map.ROUTE_RASTER_COUNT];
        for (int i = 0; i < Map.ROUTE_RASTER_COUNT; i++) {
            routePoints[i] = new Vector2();
            routeSpline.valueAt(routePoints[i], ((float)i) / ((float) Map.ROUTE_RASTER_COUNT-1));
        }

        Timeline.createSequence()
                .push(Tween.to(alpha, 1, TIME_MAP_FADE_IN)
                        .ease(TweenEquations.easeOutSine)
                        .target(1))
                .pushPause(TIME_PAUSE)
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        setCurrentStage(ANIMATE_TRAVEL);
                    }
                }))
                .push(Tween.to(animationPercent, 1, TIME_DRAW_ROUTE_TRAVELED * Math.max(roundStats.distanceTraveledPercent, 0.4f))
                        .ease(TweenEquations.easeInOutQuad)
                        .target(1))
                .pushPause(TIME_PAUSE)
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        setCurrentStage(HUD_CONTROL);
                    }
                }))
                .start(Assets.tween);

    }

    private void onHudComplete() {
        System.out.println("MapScreen | onHudComplete");
        Timeline.createSequence()
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        setCurrentStage(FADE_OUT);
                    }
                }))
                .push(Tween.to(alpha, 1, TIME_MAP_FADE_OUT)
                        .ease(TweenEquations.easeOutSine)
                        .target(0))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        LudumDare39.game.setScreen(new UpgradeScreen(playerCar.getUpgrades()));
                    }
                }))
                .start(Assets.tween);
    }

    public void setCurrentStage(MapScreen.Stage stage) {
        System.out.println("MapScreen | setCurrentStage | stage=" + stage.toString());
        this.currentStage = stage;
        this.currentStagePercent = 0f;
        this.currentStageTime = 0f;
        switch (stage) {
            case HUD_CONTROL:
                mapScreenHud.takeControl();
                break;
            case HUD_COMPLETE:
                onHudComplete();
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void update(float dt) {
//        current += dt;
        if (Gdx.input.justTouched()) {
            // TODO: speed up the route animation
        }
        currentStageTime += dt;

        // Update the currentStagePercent
        if (currentStagePercent < 1) {
            switch (currentStage) {
                case HUD_COMPLETE:
                case HUD_CONTROL:
                    // currentStageTime and % don't matter
                    break;
                case ANIMATE_TRAVEL:
                    // We want the percentage to reflect the ease, so pass that % through instead.
                    currentStagePercent = animationPercent.floatValue();
                    break;
                case FADE_IN:
                case FADE_OUT:
                    float thisStageTotalTime;
                    switch (currentStage) {
                        case FADE_IN:
                            thisStageTotalTime = TIME_MAP_FADE_IN;
                            break;
                        case FADE_OUT:
                            thisStageTotalTime = TIME_MAP_FADE_OUT;
                            break;
                        default:
                            throw new RuntimeException("Invalid stage!");
                    }
                    currentStagePercent = Math.min(1f, currentStageTime / thisStageTotalTime);
                    break;
                default:
                    throw new RuntimeException("Invalid stage!");
            }
        }

        mapScreenHud.update(dt, currentStage, currentStagePercent);
    }

    @Override
    public void render(SpriteBatch batch) {

        float splineDistance = roundStats.distanceTraveledPercent * animationPercent.floatValue();

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


        batch.begin();
        mapScreenHud.draw(batch);

        if (currentStage == Stage.FADE_IN || currentStage == Stage.FADE_OUT) {
            // On top of everything, "fade" in/out by drawing black on top of it.
            batch.setColor(0,0,0,(1 - alpha.floatValue()));
            batch.draw(Assets.whitePixel, 0, 0, Config.gameWidth, Config.gameHeight);
            batch.setColor(Color.WHITE);
        }

        batch.end();
    }

}

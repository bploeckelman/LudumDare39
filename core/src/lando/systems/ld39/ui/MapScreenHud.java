package lando.systems.ld39.ui;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.primitives.MutableFloat;
import aurelienribon.tweenengine.primitives.MutableInteger;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.objects.Stats;
import lando.systems.ld39.screens.MapScreen;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

import static lando.systems.ld39.ui.MapScreenHud.Stage.*;

public class MapScreenHud {

    public enum Stage {
        WAITING,
        CASH_IN_DISTANCE,
        CASH_IN_MONEY,
        CASH_IN_POWERUPS,
        CASH_IN_ENEMIES_SCRAPPED,
        COMPLETE
    }

    private static final float TIME_CASH_IN_PAUSE = 0.2f;
    private static final float TIME_CASH_IN_DISTANCE = .5f;
    private static final float TIME_CASH_IN_MONEY_COLLECTED = .5f;
    private static final float TIME_CASH_IN_POWERUPS = .5f;
    private static final float TIME_CASH_IN_ENEMIES_SCRAPPED = .5f;

    private static final float MONEY_PER_KM = 0.1f;
    private static final int MONEY_PER_POWERUP = 8;
    private static final int MONEY_PER_ENEMY = 16;

    private static final Vector2 HUD_FRAME_ORIGIN = new Vector2(0.1f, 0.08f); // % of w!
    private static final Vector2 HUD_FRAME_DIMENSIONS = new Vector2(0.8f, 0.3f); // % of w & h;
    private static final float HUD_FRAME_THICKNESS = 0.02f; // % of width
    private static final Color HUD_FRAME_COLOR = Color.WHITE;
    private static final Color HUD_FRAME_BACKGROUND_COLOR = new Color(0,0,0,0.6f);
    private static final float HUD_TEXT_PADDING = 0.015f; // % of width
    private static final float HUD_TEXT_STATS_WIDTH = 0.6f; // % of width within the frame & text padding.
    private static final float HUD_TEXT_MONEY_WIDTH = 0.4f; // % of width within the frame & text padding.
    private static final float HUD_TEXT_STATS_FONT_HEIGHT = 18f;

    private final Vector2 hudFrameOrigin;
    private final Vector2 hudFrameDimensions;
    private final float hudFrameThickness;
    private final float hudTextPadding;
    private final Vector2 hudTextStatsOrigin;
    private final Vector2 hudTextMoneyOrigin;
    private final float hudTextStatsWidth;
    private final float hudTextMoneyWidth;

    private float mapScreenStagePercent;
    private float distanceTraveled;

//    private float dispDistanceTraveled = 0; // we'll animate this one and show it off

    private MutableFloat displayDistanceTraveled;
    private MutableInteger displayMoneyCollected;
    private MutableInteger displayPowerupsCollected;
    private MutableInteger displayEnemiesScrapped;
    private MutableInteger displayCurrentFunds;

    private final int fundsMoneyAfterDistance;
    private final int fundsMoneyAfterMoneyCollected;
    private final int fundsMoneyAfterPowerups;
    private final int fundsMoneyAfterEnemies;

    private final MapScreen mapScreen;
    private final Stats roundStats;

    private MapScreen.Stage currentMapScreenStage;
    private MapScreenHud.Stage currentStage;
    private float currentStagePercent = 0;
    private float currentStageTime = 0;

    private final int totalFunds;

    /**
     *
     * @param roundStats
     */
    public MapScreenHud(MapScreen mapScreen, Stats roundStats) {

        this.mapScreen = mapScreen;
        this.roundStats = roundStats;

        distanceTraveled = roundStats.distanceTraveledPercent * MapScreen.DISP_ROUTE_KM;

        displayDistanceTraveled = new MutableFloat(0);  // Start at zero, as we animate this up.
        displayCurrentFunds = new MutableInteger(LudumDare39.game.gameStats.currentMoney);
        displayMoneyCollected = new MutableInteger(roundStats.moneyCollected);
        displayPowerupsCollected = new MutableInteger(roundStats.powerupsCollected);
        displayEnemiesScrapped = new MutableInteger(roundStats.enemiesScrapped);

        // Funds calculations
        fundsMoneyAfterDistance = LudumDare39.game.gameStats.currentMoney +
                ((int) (distanceTraveled * MONEY_PER_KM));
        fundsMoneyAfterMoneyCollected = fundsMoneyAfterDistance + roundStats.moneyCollected;
        fundsMoneyAfterPowerups = fundsMoneyAfterMoneyCollected + (roundStats.powerupsCollected * MONEY_PER_POWERUP);
        fundsMoneyAfterEnemies = fundsMoneyAfterPowerups + (roundStats.enemiesScrapped * MONEY_PER_ENEMY);
        // Calculate the final total funds after this round.
        totalFunds = fundsMoneyAfterEnemies;

        // Process the % based layouts
        hudFrameOrigin = new Vector2(
                Math.round(((float) Config.gameWidth) * HUD_FRAME_ORIGIN.x),
                Math.round(((float) Config.gameWidth) * HUD_FRAME_ORIGIN.y)); // NOTE: yep, both based on width
        hudFrameDimensions = new Vector2(
                Math.round(((float) Config.gameWidth) * HUD_FRAME_DIMENSIONS.x),
                Math.round(((float) Config.gameHeight) * HUD_FRAME_DIMENSIONS.y));
        hudFrameThickness = Math.round(((float) Config.gameWidth) * HUD_FRAME_THICKNESS);
        hudTextPadding = Math.round(((float) Config.gameWidth) * HUD_TEXT_PADDING);
        // Remember, text is top left!
        hudTextStatsOrigin = new Vector2(
                hudFrameOrigin.x + hudFrameThickness + hudTextPadding,
                hudFrameOrigin.y + hudFrameDimensions.y - hudFrameThickness - hudTextPadding);
        float hudTextAreaWidth = Math.round(hudFrameDimensions.x - (hudFrameThickness * 2) - (hudTextPadding * 2));
        hudTextStatsWidth = Math.round(hudTextAreaWidth * HUD_TEXT_STATS_WIDTH);
        hudTextMoneyWidth = Math.round(hudTextAreaWidth * HUD_TEXT_MONEY_WIDTH);
        hudTextMoneyOrigin = new Vector2(
                hudFrameOrigin.x + hudFrameDimensions.x - hudFrameThickness - hudTextPadding - hudTextMoneyWidth,
                hudTextStatsOrigin.y);

        setCurrentStage(WAITING);
    }


    private static String getHudStats(int iteration, float distanceTraveled, int moneyCollected, int powerupsCollected, int enemiesScrapped) {
        return "Iteration: " + iteration + "\n"
             + "Distance Traveled: " + (int) (distanceTraveled) + " km\n"
             + "Money Collected: " + moneyCollected + "\n"
             + "Powerups: " + powerupsCollected + "\n"
             + "Enemies scrapped: " + enemiesScrapped;
    }

    /**
     * It's the hud's turn to animate stuff.  Return control to the MapScreen when done.
     */
    public void takeControl() {
        Timeline t = Timeline.createSequence()
                .pushPause(TIME_CASH_IN_PAUSE)
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        setCurrentStage(CASH_IN_DISTANCE);
                    }
                }))
                .push(Timeline.createParallel()
                        .push(Tween.to(displayDistanceTraveled, 1, TIME_CASH_IN_DISTANCE)
                                .ease(TweenEquations.easeInOutSine)
                                .target(0))
                        .push(Tween.to(displayCurrentFunds, 1, TIME_CASH_IN_DISTANCE)
                                .ease(TweenEquations.easeInOutSine)
                                .target(fundsMoneyAfterDistance))
                );
        if (displayMoneyCollected.floatValue() > 0) {
            t.pushPause(TIME_CASH_IN_PAUSE)
                    .push(Tween.call(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            setCurrentStage(CASH_IN_MONEY);
                        }
                    }))
                    .push(Timeline.createParallel()
                            .push(Tween.to(displayMoneyCollected, 1, TIME_CASH_IN_MONEY_COLLECTED)
                                    .ease(TweenEquations.easeInOutSine)
                                    .target(0))
                            .push(Tween.to(displayCurrentFunds, 1, TIME_CASH_IN_MONEY_COLLECTED)
                                    .ease(TweenEquations.easeInOutSine)
                                    .target(fundsMoneyAfterMoneyCollected))
                    );
        }
        if (displayPowerupsCollected.floatValue() > 0) {
                t.pushPause(TIME_CASH_IN_PAUSE)
                    .push(Tween.call(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            setCurrentStage(CASH_IN_POWERUPS);
                        }
                    }))
                    .push(Timeline.createParallel()
                            .push(Tween.to(displayPowerupsCollected, 1, TIME_CASH_IN_POWERUPS)
                                    .ease(TweenEquations.easeInOutSine)
                                    .target(0))
                            .push(Tween.to(displayCurrentFunds, 1, TIME_CASH_IN_POWERUPS)
                                    .ease(TweenEquations.easeInOutSine)
                                    .target(fundsMoneyAfterPowerups))
                    );
        }
        if (displayEnemiesScrapped.floatValue() > 0) {
                t.pushPause(TIME_CASH_IN_PAUSE)
                    .push(Tween.call(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            setCurrentStage(CASH_IN_ENEMIES_SCRAPPED);
                        }
                    }))
                    .push(Timeline.createParallel()
                            .push(Tween.to(displayEnemiesScrapped, 1, TIME_CASH_IN_ENEMIES_SCRAPPED)
                                    .ease(TweenEquations.easeInOutSine)
                                    .target(0))
                            .push(Tween.to(displayCurrentFunds, 1, TIME_CASH_IN_ENEMIES_SCRAPPED)
                                    .ease(TweenEquations.easeInOutSine)
                                    .target(fundsMoneyAfterEnemies))
                    );
        }
                t.pushPause(TIME_CASH_IN_PAUSE)
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        setCurrentStage(MapScreenHud.Stage.COMPLETE);
                        // Update the game stats!
                        LudumDare39.game.gameStats.addRoundStats(roundStats, totalFunds);
                        // Return control back to the MapScreen
                        mapScreen.setCurrentStage(MapScreen.Stage.HUD_COMPLETE);
                    }
                }))
                .start(Assets.tween);
    }

    private void setCurrentStage(MapScreenHud.Stage newStage) {
        System.out.println("setCurrentStage, newStage=" + newStage.toString());
        this.currentStage = newStage;
        this.currentStagePercent = 0f;
        this.currentStageTime = 0f;
    }

    public void update(float dt, MapScreen.Stage stage, float stagePercent) {
        this.currentStageTime += dt;
        this.currentMapScreenStage = stage;
        this.mapScreenStagePercent = stagePercent;
    }

    public void draw(SpriteBatch batch) {
        // The background.
        batch.setColor(HUD_FRAME_BACKGROUND_COLOR);
        batch.draw(
                Assets.whitePixel,
                hudFrameOrigin.x, hudFrameOrigin.y, hudFrameDimensions.x, hudFrameDimensions.y);
        batch.setColor(HUD_FRAME_COLOR);
        Assets.defaultNinePatch.draw(batch, hudFrameOrigin.x, hudFrameOrigin.y, hudFrameDimensions.x, hudFrameDimensions.y);

        // Let's draw some text!
        if (currentMapScreenStage == MapScreen.Stage.ANIMATE_TRAVEL) {
            displayDistanceTraveled.setValue(distanceTraveled * mapScreenStagePercent);
        }
        String stats = getHudStats(
                LudumDare39.game.roundNumber,
                displayDistanceTraveled.floatValue(),
                displayMoneyCollected.intValue(),
                displayPowerupsCollected.intValue(),
                displayEnemiesScrapped.intValue());
        Assets.drawString(batch, stats,
                hudTextStatsOrigin.x, hudTextStatsOrigin.y,
                Color.WHITE, 0.38f, Assets.font, hudTextStatsWidth, Align.left);

        Assets.drawString(batch, "CURRENT FUNDS:",
                hudTextMoneyOrigin.x, hudTextMoneyOrigin.y,
                Color.WHITE, 0.4f, Assets.font, hudTextMoneyWidth, Align.center);
        Assets.drawString(batch, "$" + displayCurrentFunds.intValue(),
                hudTextMoneyOrigin.x, hudTextMoneyOrigin.y - (hudFrameDimensions.y * 0.3f),
                Color.GOLDENROD, 0.6f, Assets.font, hudTextMoneyWidth, Align.center);
    }

}

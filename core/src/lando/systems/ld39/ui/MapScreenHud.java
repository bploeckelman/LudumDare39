package lando.systems.ld39.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld39.objects.Stats;
import lando.systems.ld39.screens.MapScreen;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class MapScreenHud {

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

    private MapScreen.Stage currentStage;
    private float stagePercent;
    private float distanceTraveled;
    private float dispDistanceTraveled = 0; // we'll animate this one and show it off


    /**
     *
     * @param roundStats
     */
    public MapScreenHud(Stats roundStats) {

        this.distanceTraveled = roundStats.distanceTraveledPercent * MapScreen.DISP_ROUTE_KM;

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
    }


    private static String getHudStats(int iteration, float distanceTraveled, int moneyCollected, int powerupsCollected, int enemiesScrapped) {
        return "Iteration: " + iteration + "\n"
             + "Distance Traveled: " + (int) (distanceTraveled) + " km\n"
             + "Money Collected: " + moneyCollected + "\n"
             + "Powerups: " + powerupsCollected + "\n"
             + "Enemies scrapped: " + enemiesScrapped;

        // NOTE: String.format() isn't supported in GWT
//        return String.format(java.util.Locale.US, HUD_STATS, iteration, distanceTraveledPercent, moneyCollected, powerupsCollected, enemiesScrapped);
    }

    public void update(float dt, MapScreen.Stage stage, float stagePercent) {
        this.currentStage = stage;
        this.stagePercent = stagePercent;
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
        if (currentStage == MapScreen.Stage.ANIMATE_TRAVEL) {
            dispDistanceTraveled = distanceTraveled * stagePercent;
        }
        String stats = getHudStats(3, dispDistanceTraveled, 142, 12, 11);
        Assets.drawString(batch, stats,
                hudTextStatsOrigin.x, hudTextStatsOrigin.y,
                Color.WHITE, 0.38f, Assets.font, hudTextStatsWidth, Align.left);
    }

}

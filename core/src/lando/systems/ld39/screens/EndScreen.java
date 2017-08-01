package lando.systems.ld39.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

/**
 * Created by dsgraham on 7/31/17.
 */
public class EndScreen extends BaseScreen {

    private enum State { duel, transition1, drive_away, transition2, credits }

    private State state;
    private MutableFloat blackOverlayAlpha;
    private MutableFloat whiteOverlayAlpha;
    private Texture keyframe;
    private TextureRegion boyKeyframe;
    private TextureRegion girlKeyframe;
    private MutableFloat boyGirlCarPosX;
    private float animStateTime;
    private boolean transitioning;
    private boolean isDrivingAway;

    private final float margin = 50f;
    private final float credits_text_width = (3f / 4f) * Config.gameWidth;
    private final float boy_girl_car_target1 = margin;
    private final float boy_girl_car_target2 = 300f;
    private final float boy_girl_car_target3 = 250f;
    private final float boy_girl_car_target4 = 1200f;
    private final float boy_pos_y = 245;
    private final float girl_pos_y = 320;



    public EndScreen() {
        state = State.duel;
        blackOverlayAlpha = new MutableFloat(0f);
        whiteOverlayAlpha = new MutableFloat(0f);
        boyGirlCarPosX = new MutableFloat(-200f);
        animStateTime = 0f;
        transitioning = false;
        isDrivingAway = false;
    }

    @Override
    public void update(float dt) {
        if (state == State.duel) {
            animStateTime += dt;
            keyframe = Assets.endCutSceneAnim.getKeyFrame(animStateTime);
            if (Assets.endCutSceneAnim.isAnimationFinished(animStateTime) && !transitioning) {
                transitioning = true;
                whiteOverlayAlpha.setValue(0f);
                blackOverlayAlpha.setValue(0f);
                Timeline.createSequence()
                        .push(Tween.to(whiteOverlayAlpha, 1, 1f).target(1f))
                        .push(Tween.to(blackOverlayAlpha, 1, 1f).target(1f))
                        .push(Tween.set(whiteOverlayAlpha, 1).target(0f))
                        .push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                animStateTime = 0f;
                                state = State.drive_away;
                                isDrivingAway = true;
                            }
                        }))
                        .push(Tween.to(blackOverlayAlpha, 1, 1f).target(0f))
                        .push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                transitioning = false;
                            }
                        }))
                        .start(Assets.tween);
            }
        }

        else if (state == State.drive_away && isDrivingAway) {
            animStateTime += dt;
            keyframe = Assets.endCutSceneGrassAnim.getKeyFrame(animStateTime);
            boyKeyframe = Assets.boyCarAnim.getKeyFrame(animStateTime);
            girlKeyframe = Assets.girlCarAnim.getKeyFrame(animStateTime);

            if (!transitioning) {
                transitioning = true;
                whiteOverlayAlpha.setValue(0f);
                blackOverlayAlpha.setValue(0f);
                Timeline.createSequence()
                        .push(Tween.to(boyGirlCarPosX, 1, 1f).target(boy_girl_car_target1))
                        .pushPause(0.5f)
                        .push(Tween.to(boyGirlCarPosX, 1, 0.75f).target(boy_girl_car_target2))
                        .pushPause(0.5f)
                        .push(Tween.to(boyGirlCarPosX, 1, 1f).target(boy_girl_car_target3))
                        .push(Tween.to(boyGirlCarPosX, 1, 0.5f).target(boy_girl_car_target4))
                        .push(Tween.to(whiteOverlayAlpha, 1, 1f).target(1f))
                        .push(Tween.to(blackOverlayAlpha, 1, 1f).target(1f))
                        .push(Tween.set(whiteOverlayAlpha, 1).target(0f))
                        .push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                animStateTime = 0f;
                                state = State.credits;
                                transitioning = false;
                            }
                        }))
                        .push(Tween.to(blackOverlayAlpha, 1, 1f).target(0f))
                        .start(Assets.tween);
            }
        }

        else if (state == State.credits) {
            if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                Tween.to(blackOverlayAlpha, 1, 2)
                        .target(1)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                LudumDare39.game.setScreen(new TitleScreen());
                            }
                        })
                        .start(Assets.tween);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            if (state == State.duel) {
                renderDuel(batch);
            }
            else if (state == State.drive_away) {
                renderDriveAway(batch);
            }
            else if (state == State.credits) {
                renderCredits(batch);
            }

            batch.setColor(1f, 1f, 1f, whiteOverlayAlpha.floatValue());
            batch.draw(Assets.whitePixel, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.setColor(0f, 0f, 0f, blackOverlayAlpha.floatValue());
            batch.draw(Assets.whitePixel, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

    private void renderDuel(SpriteBatch batch) {
        final float width = 800;
        final float height = 450;
        batch.draw(keyframe, 0, (hudCamera.viewportHeight / 2f) - (height / 2f), width, height);
    }

    private void renderDriveAway(SpriteBatch batch) {
        final float width = 800;
        final float height = 450;
        batch.draw(keyframe, 0, (hudCamera.viewportHeight / 2f) - (height / 2f), width, height);
        batch.draw(boyKeyframe, boyGirlCarPosX.floatValue(), boy_pos_y);
        batch.draw(girlKeyframe, boyGirlCarPosX.floatValue(), girl_pos_y);
    }

    private String credits = ""
            + "Made with [RED]love[] for [GOLD]Ludum Dare 39[]\n"
            + "[ORANGE]Theme:[] [GOLD]Running out of POWER[]\n\n"
            + "[YELLOW]Code:[]\n"
            + "Doug Graham, Brian Rossman,\n Brian Ploeckelman\n\n"
            + "[YELLOW]Additional Code:[]\n"
            + "Ian McNamara, Colin Kennedy\n\n"
            + "[YELLOW]Art, Music, and Sound:[]\n"
            + "Tyler Pecora, Luke Bain,\nvarious artists at freesound.org\n\n\n"
            + "[PINK]Emotional Support Dog:[]\n"
            + "[ORANGE]Asuka the Shiba[]\n\n\n"
            + "Built with Lib[RED]GDX[] [PINK]<3[]";

    private void renderCredits(SpriteBatch batch) {
        Assets.eightBitFont.draw(batch, credits,
                (hudCamera.viewportWidth / 2f) - (credits_text_width / 2f),
                hudCamera.viewportHeight - margin,
                credits_text_width, Align.center, true);
    }

}

package lando.systems.ld39.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

/**
 * Created by Brian on 7/25/2017
 */
public class TitleScreen extends BaseScreen {

    public float accum;
    public float animStateTime = 0f;
    public Texture keyframe;

    public TitleScreen() {
        accum = 0f;
    }

    @Override
    public void update(float dt) {
        accum += dt;

        animStateTime += dt;
        keyframe = Assets.titleScreenAnim.getKeyFrame(animStateTime);

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            Tween.to(alpha, 1, 1)
                    .target(1)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            LudumDare39.game.setScreen(new IntroScreen());

                        }
                    })
                    .start(Assets.tween);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        float clickAlpha = MathUtils.clamp(.85f + (float)Math.sin(accum * 7) * .5f, .2f, 1f);

        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.draw(keyframe, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
            Assets.drawString(batch, "Made for Ludum Dare 39: 'Running out of power'", 0, 90f, new Color(0.2f, 0.2f, 1, 1), .35f, Assets.font, hudCamera.viewportWidth, Align.center);
            Assets.drawString(batch, "Click to Start", 0, 30f, new Color(0, 1, 0, clickAlpha), .4f, Assets.font, hudCamera.viewportWidth, Align.center);

            batch.setColor(0, 0, 0, alpha.floatValue());
            batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

}

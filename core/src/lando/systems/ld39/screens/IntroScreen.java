package lando.systems.ld39.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class IntroScreen extends BaseScreen {

    String story = "The president (who is also your girlfriend) has been kidnapped...\n\nby Elon Musk!!!\n\n"
                 + "You must upgrade your shitty custom electric car until it's hardcore enough to get you from Washington DC to Musk H.Q. in Nevada and take him out!\n\n"
                 + "Are you a rad enough entrepreneur to rescue the president / your girlfriend?";

    TextureRegion keyframe;
    float animStateTime = 0f;
    float accum = 0f;

    int currentCharIndex = 0;
    boolean storyPrinted = false;

    final float margin = 50f;
    final float char_display_threshold = 0.075f;
    final float story_text_width = Config.gameWidth / 1.25f;

    public IntroScreen() {
        accum = char_display_threshold;
    }

    @Override
    public void update(float dt) {
        accum += dt;
        if (accum >= char_display_threshold) {
            accum -= char_display_threshold;

            if (++currentCharIndex >= story.length() - 1) {
                currentCharIndex = story.length() - 1;
                storyPrinted = true;
            }
        }

        animStateTime += dt;
        keyframe = Assets.carTalkAnim.getKeyFrame(animStateTime);

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            if (!storyPrinted) {
                storyPrinted = true;
                currentCharIndex = story.length() - 1;
            } else {
                Tween.to(alpha, 1, 1)
                        .target(1)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                LudumDare39.game.setScreen(new GameScreen());

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
            Assets.eightBitFont.draw(batch, story.substring(0, currentCharIndex), margin, hudCamera.viewportHeight - margin, story_text_width, Align.topLeft, true);

            final float width = 3f * keyframe.getRegionWidth();
            final float height = 3f * keyframe.getRegionHeight();
            batch.draw(keyframe, hudCamera.viewportWidth - width - margin, margin, width, height);
            batch.setColor(0, 0, 0, alpha.floatValue());
            batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

            batch.setColor(Color.WHITE);
        }
        batch.end();

    }
}

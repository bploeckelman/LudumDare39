package lando.systems.ld39;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import lando.systems.ld39.screens.BaseScreen;
import lando.systems.ld39.screens.MapScreen;
import lando.systems.ld39.utils.Assets;

/**
 * Created by Brian on 7/25/2017
 */
public class LudumDare39 extends ApplicationAdapter {

	public static LudumDare39 game;

	private BaseScreen screen;

	@Override
	public void create () {
		Assets.load();
		float progress = 0f;
		do {
			progress = Assets.update();
		} while (progress != 1f);
		game = this;

		setScreen(new MapScreen());
	}

	@Override
	public void render () {
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
		Assets.tween.update(dt);
		screen.update(dt);
		screen.render(Assets.batch);
//        Gdx.app.log("Render Calls", "" + Assets.batch.renderCalls);
	}

	@Override
	public void dispose () {
		Assets.dispose();
	}

	public void setScreen(BaseScreen newScreen){
		screen = newScreen;
	}

}

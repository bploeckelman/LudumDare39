package lando.systems.ld39;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import lando.systems.ld39.objects.Stats;
import lando.systems.ld39.screens.BaseScreen;
import lando.systems.ld39.screens.TitleScreen;
import lando.systems.ld39.utils.Assets;

/**
 * Created by Brian on 7/25/2017
 */
public class LudumDare39 extends ApplicationAdapter {

	public static LudumDare39 game;

	public int roundNumber = 1;
	public Stats gameStats;

	private BaseScreen screen;

	@Override
	public void create () {
		Assets.load();
		float progress = 0f;
		do {
			progress = Assets.update();
		} while (progress != 1f);
		game = this;

		gameStats = new Stats();

		setScreen(new TitleScreen());
//		Stats roundStats = new Stats();
//		roundStats.distanceTraveledPercent = 0.34f;
//		roundStats.currentMoney = 128;
//		roundStats.moneyCollected = 256;
//		roundStats.enemiesScrapped = 4;
//		roundStats.powerupsCollected = 4;
//		setScreen( new MapScreen(roundStats, new PlayerCar(new GameScreen())));
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

package lando.systems.ld39.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.utils.Config;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Config.gameWidth;
		config.height = Config.gameHeight;
		config.resizable = Config.resizable;
		new LwjglApplication(new LudumDare39(), config);
	}
}

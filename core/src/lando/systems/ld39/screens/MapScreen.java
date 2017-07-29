package lando.systems.ld39.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

public class MapScreen extends BaseScreen {

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(Assets.map, 0, 0, Config.gameWidth, Config.gameHeight);
        batch.end();
    }

}

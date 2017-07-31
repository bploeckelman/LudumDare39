package lando.systems.ld39.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld39.objects.GameItem;
import lando.systems.ld39.objects.Item;
import lando.systems.ld39.objects.Upgrades;
import lando.systems.ld39.utils.accessors.*;

/**
 * Created by Brian on 7/25/2017.
 */
public class Assets {

    public static AssetManager mgr;
    public static TweenManager tween;
    public static SpriteBatch batch;
    public static ShapeRenderer shapes;
    public static GlyphLayout layout;
    public static BitmapFont font;
    public static ShaderProgram fontShader;

    public static ShaderProgram roadShader;
    public static Texture grassTexture;
    public static Texture roadTexture;
    public static Texture gravelTexture;
    public static Texture desertTexture;

    public static TextureAtlas atlas;
    public static NinePatch defaultNinePatch;

    public static TextureRegion testTexture;
    public static TextureRegion whitePixel;

    public static TextureRegion carBase;

    public static TextureRegion garageBackground;
    public static TextureRegion upgradeIconBattery;
    public static TextureRegion upgradeIconMotor;
    public static TextureRegion upgradeIconBooster;
    public static TextureRegion upgradeIconWeapon;
    public static TextureRegion upgradeIconHull;
    public static TextureRegion upgradeIconTire;

    public static Texture map;

    public static TextureRegion basicProjectileTex;
    public static TextureRegion zappaTex;
    public static TextureRegion missileTex;

    public static ShaderProgram hudShader;
    public static TextureRegion lightningTexture;
    public static TextureRegion healthTexture;

    public static boolean initialized;

    public static void load() {
        initialized = false;

        final TextureLoader.TextureParameter linearParams = new TextureLoader.TextureParameter();
        linearParams.minFilter = Texture.TextureFilter.Linear;
        linearParams.magFilter = Texture.TextureFilter.Linear;

        final TextureLoader.TextureParameter nearestParams = new TextureLoader.TextureParameter();
        nearestParams.minFilter = Texture.TextureFilter.Nearest;
        nearestParams.magFilter = Texture.TextureFilter.Nearest;

        mgr = new AssetManager();
        mgr.load("images/usa-map-v1.png", Texture.class);

        atlas = new TextureAtlas(Gdx.files.internal("sprites.atlas"));

        if (tween == null) {
            tween = new TweenManager();
            Tween.setCombinedAttributesLimit(4);
            Tween.registerAccessor(Color.class, new ColorAccessor());
            Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
            Tween.registerAccessor(Vector2.class, new Vector2Accessor());
            Tween.registerAccessor(Vector3.class, new Vector3Accessor());
            Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        }

        mgr.load("images/grass.png", Texture.class);
        mgr.load("images/road.png", Texture.class);
        mgr.load("images/gravel.png", Texture.class);
        mgr.load("images/desert.png", Texture.class);




        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();
    }

    public static float update() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;
        initialized = true;

        grassTexture = mgr.get("images/grass.png");
        grassTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        roadTexture = mgr.get("images/road.png");
        roadTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        gravelTexture = mgr.get("images/gravel.png");
        gravelTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        desertTexture = mgr.get("images/desert.png");
        desertTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        testTexture = atlas.findRegion("badlogic");
        whitePixel = atlas.findRegion("white-pixel");

        // loads images in updates
        Item.load();
        GameItem.load();

        carBase = atlas.findRegion("car-base");

        garageBackground = atlas.findRegion("upgrade-garage-bg");
        upgradeIconBattery = atlas.findRegion("upgrade-icon-battery");
        upgradeIconMotor = atlas.findRegion("upgrade-icon-motor");
        upgradeIconBooster = atlas.findRegion("upgrade-icon-booster");
        upgradeIconWeapon = atlas.findRegion("upgrade-icon-weapon");
        upgradeIconHull = atlas.findRegion("upgrade-icon-hull");
        upgradeIconTire = atlas.findRegion("upgrade-icon-tire");
        lightningTexture = atlas.findRegion("lightning");
        healthTexture = atlas.findRegion("health");

        basicProjectileTex = atlas.findRegion("basicProjectile");
        zappaTex = atlas.findRegion("ZappaProjectile1");
        missileTex = atlas.findRegion("rocket");

        map = mgr.get("images/usa-map-v1.png", Texture.class);

        defaultNinePatch = new NinePatch(atlas.findRegion("ninepatch"), 6,6,6,6);

        final Texture distText = new Texture(Gdx.files.internal("fonts/ubuntu.png"), true);
        distText.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("fonts/ubuntu.fnt"), new TextureRegion(distText), false);
        font.getData().setScale(.3f);

        fontShader = loadShader("shaders/dist.vert", "shaders/dist.frag");

        roadShader = loadShader("shaders/default.vert", "shaders/road.frag");
        hudShader = loadShader("shaders/default.vert", "shaders/huditem.frag");
        return 1f;
    }


    public static void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        mgr.clear();
    }

    private static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));
        ShaderProgram.pedantic = true;

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + shaderProgram.getLog());
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + shaderProgram.getLog());
        } else {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log:\n" + shaderProgram.getLog());
        }

        return shaderProgram;
    }

    public static void drawString(SpriteBatch batch, String text, float x, float y, Color c, float scale, BitmapFont font){
        batch.setShader(fontShader);
        fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y);
        font.getData().setScale(1f);
        fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

    public static void drawString(SpriteBatch batch, String text, float x, float y, Color c, float scale, BitmapFont font, float targetWidth, int halign){
        batch.setShader(fontShader);
        fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y, targetWidth, halign, true);
        font.getData().setScale(1f);
        fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

    public static Color hsvToRgb(float hue, float saturation, float value, Color outColor) {
        if (outColor == null) outColor = new Color();

        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: outColor.set(value, t, p, 1f); break;
            case 1: outColor.set(q, value, p, 1f); break;
            case 2: outColor.set(p, value, t, 1f); break;
            case 3: outColor.set(p, q, value, 1f); break;
            case 4: outColor.set(t, p, value, 1f); break;
            case 5: outColor.set(value, p, q, 1f); break;
            default: throw new GdxRuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
        return outColor;
    }

}

package lando.systems.ld39.objects;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld39.utils.Config;

public class Map {

    private static final int MAP_IMAGE_WIDTH = 640;
    private static final int MAP_IMAGE_HEIGHT = 480;
    public static final int MAP_DRAW_WIDTH = Config.gameWidth;
    public static final int MAP_DRAW_HEIGHT = Config.gameHeight;
    public static final int ROUTE_RASTER_COUNT = 100;

    public static final Vector2[] ROUTE_POINTS = new Vector2[]{
            getScaledMapVector(540, 216),
            getScaledMapVector(540, 216),
            getScaledMapVector(520, 196),
            getScaledMapVector(504, 197),
            getScaledMapVector(483, 185),
            getScaledMapVector(473, 189),
            getScaledMapVector(463, 184),
            getScaledMapVector(419, 192),
            getScaledMapVector(399, 201),
            getScaledMapVector(360, 193),
            getScaledMapVector(328, 201),
            getScaledMapVector(319, 210),
            getScaledMapVector(283, 209),
            getScaledMapVector(272, 200),
            getScaledMapVector(259, 200),
            getScaledMapVector(235, 195),
            getScaledMapVector(223, 197),
            getScaledMapVector(206, 185),
            getScaledMapVector(162, 189),
            getScaledMapVector(146, 201),
            getScaledMapVector(140, 196),
            getScaledMapVector(128, 196),
            getScaledMapVector(117, 184),
            getScaledMapVector( 99, 187),
            getScaledMapVector( 85, 178),
            getScaledMapVector( 70, 198),
            getScaledMapVector( 65, 198),
            getScaledMapVector( 65, 198)
    };

    private Map() {}


    /**
     * Converts x/y screen coordinates from Photoshop into x/y that will work for this map in the game.
     * @param x x
     * @param y The y coordinate of the point on the original image measured from the TOP (will be inverted)
     * @return The vector
     */
    private static Vector2 getScaledMapVector(int x, int y) {
        int modifiedX = Math.round(((float) x) / ((float) MAP_IMAGE_WIDTH) * ((float) MAP_DRAW_WIDTH));
        int modifiedY = Math.round(((float) (MAP_IMAGE_HEIGHT - y)) / ((float) MAP_IMAGE_HEIGHT) * ((float) MAP_DRAW_HEIGHT));
        return new Vector2(modifiedX, modifiedY);
    }


}

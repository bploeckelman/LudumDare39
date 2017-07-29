package lando.systems.ld39.road;


/**
 * Created by dsgraham on 7/29/17.
 */
public class RoadDef {

    public static RoadDef center = new RoadDef(200, 400);
    public static RoadDef left = new RoadDef(100, 300);

    public float leftSide;
    public float width;

    public RoadDef(float leftSide, float width ){
        this.leftSide = leftSide;
        this.width = width;
    }
}

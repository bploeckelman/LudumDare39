package lando.systems.ld39.road;


/**
 * Created by dsgraham on 7/29/17.
 */
public class RoadDef {

    public static RoadDef center = new RoadDef(200, 400);
    public static RoadDef thin = new RoadDef(300, 200);
    public static RoadDef left = new RoadDef(100, 250);
    public static RoadDef right = new RoadDef(450, 250);


    public float leftSide;
    public float width;

    public RoadDef(float leftSide, float width ){
        this.leftSide = leftSide;
        this.width = width;
    }
}

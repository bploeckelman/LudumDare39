package lando.systems.ld39.road;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

/**
 * Created by dsgraham on 7/29/17.
 */
public class Road {

    public float segmentLength = 400;
    public Array<RoadDef> roadSegments;
    public ShapeRenderer shapes;

    public Road(){
        shapes = new ShapeRenderer();
        shapes.setAutoShapeType(true);
        roadSegments = new Array<RoadDef>();
        for (int i = 0; i < 100; i ++) {
            roadSegments.add(RoadDef.center);
            roadSegments.add(RoadDef.left);
            roadSegments.add(RoadDef.left);
            roadSegments.add(RoadDef.center);
        }
    }

    public void update(float dt) {

    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        batch.end();
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.WHITE);
        float cameraBottom = camera.position.y - camera.viewportHeight/2f;
        for (int i = 0; i < (camera.viewportHeight / segmentLength) +1; i++){
            int roadIndex = (int)(cameraBottom / segmentLength) + i;
            if (roadIndex < 0 || roadIndex >= roadSegments.size) continue;
            RoadDef current = roadSegments.get(roadIndex);
            RoadDef next = roadSegments.get(roadIndex + 1);
            shapes.triangle(current.leftSide, roadIndex * segmentLength,
                            current.leftSide + current.width, roadIndex * segmentLength,
                            next.leftSide, (roadIndex + 1) * segmentLength);
            shapes.triangle(current.leftSide + current.width, roadIndex * segmentLength,
                            next.leftSide, (roadIndex + 1) * segmentLength,
                            next.leftSide + next.width, (roadIndex +1) * segmentLength);
        }

        shapes.end();

        batch.begin();
    }
}

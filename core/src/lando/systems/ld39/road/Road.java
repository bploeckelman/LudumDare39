package lando.systems.ld39.road;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld39.utils.Assets;
import lando.systems.ld39.utils.Config;

import java.util.Random;

/**
 * Created by dsgraham on 7/29/17.
 */
public class Road {
    public Random rand = new Random();
    public float segmentLength = 400;
    public float shoulderWidth = 70;
    public Array<RoadDef> roadSegments;
    public ShapeRenderer shapes;
    public Texture fboTexture;
    public FrameBuffer fbo;

    public Road(){
        fbo = new FrameBuffer(Pixmap.Format.RGB888, Config.gameWidth, Config.gameHeight, false);
        fboTexture = fbo.getColorBufferTexture();
        shapes = new ShapeRenderer();
        shapes.setAutoShapeType(true);
        roadSegments = new Array<RoadDef>();
        generateRoad();
    }

    public void update(float dt) {

    }

    public void renderFrameBuffer(SpriteBatch batch, OrthographicCamera camera){
        fbo.begin();
        batch.end();
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.BLACK);
        float cameraBottom = camera.position.y - camera.viewportHeight/2f;
        shapes.rect(0, cameraBottom, camera.viewportWidth, camera.viewportHeight);
        shapes.setColor(Color.WHITE);
        for (int i = 0; i < (camera.viewportHeight / segmentLength) +1; i++){
            int roadIndex = (int)(cameraBottom / segmentLength) + i;
            RoadDef current = getRoadDef(roadIndex);
            RoadDef next = getRoadDef(roadIndex+1);

            // Road
            shapes.triangle(current.leftSide, roadIndex * segmentLength,
                    current.leftSide + current.width, roadIndex * segmentLength,
                    next.leftSide, (roadIndex + 1) * segmentLength);
            shapes.triangle(current.leftSide + current.width, roadIndex * segmentLength,
                    next.leftSide, (roadIndex + 1) * segmentLength,
                    next.leftSide + next.width, (roadIndex +1) * segmentLength);
            // Left Side
            shapes.triangle(current.leftSide - shoulderWidth, roadIndex * segmentLength,
                    current.leftSide, roadIndex * segmentLength,
                    next.leftSide - shoulderWidth, (roadIndex +1) *segmentLength,
                    Color.BLACK, Color.WHITE, Color.BLACK);
            shapes.triangle(current.leftSide, roadIndex * segmentLength,
                    next.leftSide - shoulderWidth, (roadIndex + 1) * segmentLength,
                    next.leftSide, (roadIndex + 1) * segmentLength,
                    Color.WHITE, Color.BLACK, Color.WHITE);

            // Right Side
            shapes.triangle(current.leftSide + current.width, roadIndex * segmentLength,
                    current.leftSide + current.width + shoulderWidth, roadIndex * segmentLength,
                    next.leftSide + next.width, (roadIndex + 1) * segmentLength,
                    Color.WHITE, Color.BLACK, Color.WHITE);
            shapes.triangle(current.leftSide + current.width + shoulderWidth, roadIndex * segmentLength,
                    next.leftSide + next.width, (roadIndex + 1) * segmentLength,
                    next.leftSide + next.width + shoulderWidth, (roadIndex + 1) * segmentLength,
                    Color.BLACK, Color.WHITE, Color.BLACK);
        }

        shapes.end();

        batch.begin();
        fbo.end();
    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        batch.setShader(Assets.roadShader);

        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE3);
        Assets.gravelTexture.bind(3);
        Assets.roadShader.setUniformi("u_texture4", 3);


        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE2);
        Assets.roadTexture.bind(2);
        Assets.roadShader.setUniformi("u_texture3", 2);


        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE1);
        Assets.grassTexture.bind(1);

        Assets.roadShader.setUniformi("u_texture2", 1);

        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
        fboTexture.bind(0);
        Assets.roadShader.setUniformi("u_texture", 0); 

        Assets.roadShader.setUniformf("u_camera", camera.position.x / camera.viewportWidth, camera.position.y/ camera.viewportHeight);

        batch.draw(fboTexture, 0, camera.position.y + camera.viewportHeight/2f, camera.viewportWidth, - camera.viewportHeight);
        batch.setShader(null);
    }

    public boolean isOnRoad(float pnt_x, float pnt_y){
        int roadIndex = (int)(pnt_y / segmentLength);
        RoadDef current = getRoadDef(roadIndex);
        RoadDef next = getRoadDef(roadIndex + 1);
        if (Intersector.isPointInTriangle(pnt_x, pnt_y,
                current.leftSide, roadIndex * segmentLength,
                current.leftSide + current.width, roadIndex * segmentLength,
                next.leftSide, (roadIndex + 1) * segmentLength)){
            return true;
        }
        if (Intersector.isPointInTriangle(pnt_x, pnt_y,
                current.leftSide + current.width, roadIndex * segmentLength,
                next.leftSide, (roadIndex + 1) * segmentLength,
                next.leftSide + next.width, (roadIndex +1) * segmentLength)){
            return true;
        }
        return false;
    }

    private RoadDef getRoadDef(int i){
        if (i < 0) return RoadDef.center;
        if (i >= roadSegments.size) return RoadDef.center;
        return roadSegments.get(i);
    }

    private void generateRoad(){
        roadSegments.clear();
        roadSegments.add(RoadDef.center);
        roadSegments.add(RoadDef.center);
        roadSegments.add(RoadDef.center);
        roadSegments.add(RoadDef.center);
        for (int i = 0; i < 100; i++){
            int type = rand.nextInt(4);
            int count = rand.nextInt(10) + 1;
            for (int j = 0; j < count; j++){
                switch (type) {
                    case 0:
                        roadSegments.add(RoadDef.center);
                        break;
                    case 1:
                        roadSegments.add(RoadDef.left);
                        break;
                    case 2:
                        roadSegments.add(RoadDef.right);
                        break;
                    case 3:
                        roadSegments.add(RoadDef.thin);
                        break;
                }
            }
        }
    }
}

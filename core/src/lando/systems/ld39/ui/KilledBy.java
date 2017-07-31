package lando.systems.ld39.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld39.utils.Assets;

public class KilledBy {

    public String what;
    public TextureRegion image;
    public Rectangle container;
    public Rectangle textContainer;
    public Rectangle imageContainer;

    final float margin = 10f;
    final float image_margin = 20f;

    public KilledBy(String what, TextureRegion image, OrthographicCamera hudCamera) {
        this.what = what;
        this.image = image;

        float containerWidth = (3f / 4f) * hudCamera.viewportWidth;
        float containerHeight = (1f / 3f) * hudCamera.viewportHeight;
        this.container = new Rectangle(
                (hudCamera.viewportWidth / 2f) - (containerWidth / 2f),
                (hudCamera.viewportHeight / 2f) - (containerHeight / 2f),
                containerWidth, containerHeight
        );

        float textContainerWidth = (2f / 3f) * containerWidth - 2f * margin;
        float textContainerHeight = containerHeight - 2f * margin;
        this.textContainer = new Rectangle(
                container.x + margin, container.y + margin,
                textContainerWidth, textContainerHeight
        );

        float imageContainerWidth = (1f / 3f) * containerWidth - 2f * margin;
        float imageContainerHeight = containerHeight - 2f * margin;
        this.imageContainer = new Rectangle(
                textContainer.x + textContainerWidth + margin, container.y + margin,
                imageContainerWidth, imageContainerHeight
        );
    }

    public void render(SpriteBatch batch) {
        // Draw container
        batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        batch.draw(Assets.whitePixel, container.x, container.y, container.width, container.height);
        batch.setColor(Color.WHITE);
        Assets.defaultNinePatch.draw(batch, container.x, container.y, container.width, container.height);

        // Draw what text
        Assets.drawString(batch, "Run ended by:",
                textContainer.x, textContainer.y + textContainer.height / 2f + 40f,
                Color.WHITE, 0.4f, Assets.font, textContainer.width, Align.center
        );
        Assets.drawString(batch, what,
                textContainer.x, textContainer.y + textContainer.height / 2f,
                Color.RED, 0.6f, Assets.font, textContainer.width, Align.center
        );

        // Draw image (if available)
        if (image != null) {
            Assets.defaultNinePatch.draw(batch, imageContainer.x, imageContainer.y, imageContainer.width, imageContainer.height);

            float srcWidth = image.getRegionWidth();
            float srcHeight = image.getRegionHeight();
            float dstHeight = imageContainer.height - 2f * image_margin;
            float aspect = srcWidth / srcHeight;
            float dstWidth = dstHeight * aspect;

            batch.draw(image,
                    imageContainer.x + (imageContainer.width / 2f) - (dstWidth / 2f),
                    imageContainer.y + (imageContainer.height / 2f) - (dstHeight / 2f),
                    dstWidth, dstHeight);
        }
    }

}

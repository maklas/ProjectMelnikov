package ru.maklas.melnikov.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Consumer;

public class ImageAssets extends Asset{

    public TextureRegion empty;
    public TextureRegion cell;
    public Texture wang;
    public TextureRegion pixel;
    public TextureRegion circle;
    public BitmapFont font;

	@Override
    protected void loadImpl() {
        empty = new TextureRegion(new Texture("default.png"));
        cell = new TextureRegion(new Texture("cell.png"));
        wang = new Texture("wang.png");
        circle = new TextureRegion(new Texture("circle.png"));


        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixel = createRectangleImage(1, 1, Color.WHITE);
    }

    @Override
    protected void disposeImpl() {
        empty.getTexture().dispose();
    }

    private static TextureRegion[] split(String path, int horizontal, int vertical) {
        return split(path, horizontal, vertical, horizontal * vertical);
    }

    private static TextureRegion[] split(String path, int horizontal, int vertical, int total){
        Texture texture = new Texture(path);
        TextureRegion[] regions = new TextureRegion[total];
        int width = texture.getWidth() / vertical;
        int height = texture.getHeight() / horizontal;

        int id = 0;
        for (int i = 0; i < horizontal; i++) {
            for (int j = 0; j < vertical; j++) {
                int x = width * j;
                int y = height * i;
                regions[id++] = new TextureRegion(texture, x, y, width, height);
                if (id >= total) return regions;
            }
        }
        return regions;
    }


    public static TextureRegion createCircleImage(int radius, Color color){
        int size = radius * 2 + 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.fill();
        pixmap.setColor(color);
        pixmap.fillCircle(radius + 1, radius + 1, radius);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createCircleImageNoFill(int radius, Color color){
        int size = radius * 2 + 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.drawCircle(radius + 1, radius + 1, radius);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createImage(int width, int height, Color color, Consumer<Pixmap> pixmapConsumer){
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmapConsumer.accept(pixmap);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createRectangleImage(int width, int height, Color color){
        Pixmap pixmap = new Pixmap(width , height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createRectangleImage(int width, int height, Color color, Color borderColor){
        Pixmap pixmap = new Pixmap(width + 2, height + 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(1, 1, width, height);
        pixmap.setColor(borderColor);
        pixmap.drawRectangle(1, 1, width,  height);
        return new TextureRegion(new Texture(pixmap));
    }

    public static void draw(Batch batch, TextureRegion region, float x, float y, float pivotX, float pivotY, float scaleX, float scaleY, float angle) {
        int width = region.getRegionWidth();
        int height = region.getRegionHeight();
        float originX = width * pivotX;
        float originY = height * pivotY;

        batch.draw(region,
                x - originX, y - originY,
                originX, originY,
                width, height,
                scaleX, scaleY,
                angle + angle);
    }
}

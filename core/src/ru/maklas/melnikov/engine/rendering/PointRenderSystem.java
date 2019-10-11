package ru.maklas.melnikov.engine.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.melnikov.assets.A;
import ru.maklas.melnikov.assets.ImageAssets;
import ru.maklas.melnikov.engine.B;
import ru.maklas.melnikov.engine.M;
import ru.maklas.melnikov.engine.point.PointComponent;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class PointRenderSystem extends RenderEntitySystem implements YScalable {

    private ImmutableArray<Entity> points;
    private Batch batch;
    private OrthographicCamera cam;
    private float mouseOverDistance = 10;
    private double yScale = 10;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        points = entitiesFor(PointComponent.class);
        batch = engine.getBundler().get(B.batch);
        cam = engine.getBundler().get(B.cam);
    }

    @Override
    public void render() {
        if (points.size() == 0) return;
        Vector2 mouse = Utils.getMouse(cam);
        final float MOD = mouseOverDistance * cam.zoom;

        batch.begin();

        for (Entity point : points) {
            PointComponent pp = point.get(M.cross);
            Color color = pp.colorOverride != null ? pp.colorOverride : pp.type.getColor();
            batch.setColor(color);
            float scale = 0.25f * cam.zoom;
            float x = point.x;
            float y = (float) (point.y / yScale);
            ImageAssets.draw(batch, A.images.circle, x, y, 0.5f, 0.5f, scale, scale, 0);
            if (mouse.dst(x, y) < MOD){
                String text = Utils.vec1.set(x, point.y).toString();
                BitmapFont font = A.images.font;
                font.setColor(color);
                font.draw(batch, text, x + 10 * cam.zoom, y - 50 * cam.zoom);
            }
        }

        batch.end();
    }

    @Override
    public PointRenderSystem setYScale(double yScale){
        this.yScale = yScale;
        return this;
    }
}

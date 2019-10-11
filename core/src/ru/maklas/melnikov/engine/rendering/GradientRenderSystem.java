package ru.maklas.melnikov.engine.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.melnikov.engine.B;
import ru.maklas.melnikov.engine.M;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class GradientRenderSystem extends RenderEntitySystem {

    public enum Mode {POWER, ANGLE, ZERO_TO_ONE, MINUS_ONE_TO_ONE};
    private ShapeRenderer sr;
    private ImmutableArray<Entity> entities;
    private OrthographicCamera cam;
    private double max = 1_000_000;
    private double min = Double.MAX_VALUE;
    private Mode mode = Mode.MINUS_ONE_TO_ONE;
    private static final double intensityPower = 1; //Makes closer to black
    private static final double sharpness = 1000; //1...1000
    private static final boolean revert = true;

    @Override
    public void onAddedToEngine(Engine engine) {
        sr = engine.getBundler().get(B.sr);
        entities = entitiesFor(BiFunctionComponent.class);
        cam = engine.getBundler().get(B.cam);
    }

    @Override
    public void render() {
        if (!Gdx.input.isKeyPressed(Input.Keys.G)) return;
        if (entities.size() == 0) return;

        float leftX = Utils.camLeftX(cam);
        float rightX = Utils.camRightX(cam);
        float botY = Utils.camBotY(cam);
        float topY = Utils.camTopY(cam);
        float step = cam.zoom;
        ShapeRenderer sr = this.sr;
        double min = this.min;
        double max = this.max;
        double newMin = Double.MAX_VALUE;
        double newMax = -Double.MAX_VALUE;

        sr.begin(ShapeRenderer.ShapeType.Point);
        {
            float x = leftX;
            while (x < rightX){
                float y = botY;
                while (y < topY) {
                    double val = entities.get(0).get(M.biFun).fun.f(x, y);
                    for (int i = 1; i < entities.size(); i++) {
                        val = Math.max(val, entities.get(i).get(M.biFun).fun.f(x, y));
                        // +, *, max
                    }
                    if (val > newMax){
                        newMax = val;
                    }
                    if (val < newMin){
                        newMin = val;
                    }

                    double intensity; //0..1

                    switch (mode){
                        case POWER:
                            intensity = ((val - min) / (max - min));
                            //intensity = intensity > 1 ? 1 : Math.pow(intensity, power);
                            break;
                        case ANGLE:
                            intensity = ((val - min) / (max - min));
                            intensity = intensity > 0.5 ? 1 : 0;
                            break;
                        case ZERO_TO_ONE:
                            intensity = MathUtils.clamp(val, 0, 1);
                            break;
                        case MINUS_ONE_TO_ONE:
                            intensity = MathUtils.clamp(val + 0.5, 0, 1);
                            break;
                        default:
                            throw new RuntimeException("Unknwon  type");
                    }
                    if (sharpness > 1){
                    	intensity = sharpner(intensity, sharpness);
					}
                    if (intensityPower != 1.0){
                        intensity = Math.pow(intensity, intensityPower);
                    }
                    if (revert){
                        intensity = 1 - intensity;
                    }
                    sr.setColor((float) intensity, (float) intensity, (float) intensity, 1);
                    sr.point(x, y, 0);
                    y += step;
                }
                x += step;
            }
        }
        sr.end();
        this.max = newMax;
        this.min = newMin;
    }



	public static double sharpner(double value, double sharpness){
		return 1 / (1 + Math.pow(Math.pow(Math.E, sharpness), -value + 0.5));
	}
}

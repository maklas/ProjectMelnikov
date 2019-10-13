package ru.maklas.melnikov.engine.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.libs.Timer;
import ru.maklas.melnikov.assets.A;
import ru.maklas.melnikov.assets.ImageAssets;
import ru.maklas.melnikov.engine.B;
import ru.maklas.melnikov.engine.M;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

import static java.lang.Math.*;

/** Рендерит функцию с двумя параметрами, если у неё присутствует gradient функция **/
public class BiFunctionRenderSystem extends RenderEntitySystem {

    private static final double MAX_DRAWING_LOOSENESS = 4;
    private static final boolean DRAW_GRADIENT_LINES = false; //Линии уровня
    private static final int GRADIENT_LINES_COUNT = 5;
    private static final int GRADIENT_LINE_INITIAL_VALUE = 50;
    private static final int GRADIENT_LINE_DELTA = 125;

    private OrthographicCamera cam;
    private ShapeRenderer sr;
    private Batch batch;
    private ImmutableArray<Entity> functions;
    private double drawingLooseness = 1;
    private Timer drawingPrecisionStabilizationTimer;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        cam = engine.getBundler().get(B.cam);
        sr = engine.getBundler().get(B.sr);
        batch = engine.getBundler().get(B.batch);
        functions = entitiesFor(BiFunctionComponent.class);
        drawingPrecisionStabilizationTimer = new Timer(0.3f, false, () -> drawingLooseness = 1);
        drawingPrecisionStabilizationTimer.setEnabled(false);
    }

    @Override
    public void render() {
        updatePrecision();

        sr.begin(ShapeRenderer.ShapeType.Point);
        for (Entity function : functions) {
            BiFunctionComponent bfc = function.get(M.biFun);
            sr.setColor(bfc.color);
            drawFunction(bfc);
        }

        sr.end();
    }

    /** Checks camera position and reduces drawing precision when it moves **/
    private void updatePrecision() {
        drawingPrecisionStabilizationTimer.update(engine.getLastDt());
        ImmutableArray<Entity> cameras = engine.entitiesFor(CameraComponent.class);
        if (cameras.size() == 0) {
            drawingLooseness = 1;
        } else {
            CameraComponent cc = cameras.get(0).get(M.camera);
            if (cc.vX != 0 || cc.vY != 0){
                drawingLooseness = MAX_DRAWING_LOOSENESS;
                drawingPrecisionStabilizationTimer.reset();
            }
        }
    }

    private void drawFunction(BiFunctionComponent bfc){
        double rightX = Utils.camRightX(cam);
        double leftX = Utils.camLeftX(cam);
        double botY = Utils.camBotY(cam);
        double topY = Utils.camTopY(cam);
        double step = cam.zoom;

        double resolutionMultiplier = bfc.resolutionMultiplier * drawingLooseness;
        double thickness = bfc.thickness;
        double valMinMax = step * resolutionMultiplier * thickness;

        for (double x = leftX; x < rightX; x += step * resolutionMultiplier) {
            for (double y = botY; y < topY; y += step * resolutionMultiplier) {
                double val = bfc.fun.absF(x, y);
                double gradient = bfc.fun.g(x, y);

                double gradValue = GRADIENT_LINE_INITIAL_VALUE;

                boolean hits = val < gradient * valMinMax;
                if (hits){
                    drawSr(sr, x, y, 1);
                } else if (DRAW_GRADIENT_LINES) {
                    for (int i = 0; i < GRADIENT_LINES_COUNT; i++) {
                        hits = val - gradValue < (gradient * valMinMax) && val - (gradValue - (valMinMax * gradient)) > gradient * valMinMax;
                        gradValue += GRADIENT_LINE_DELTA;
                        if (hits) {
                            drawSr(sr, x, y, 1);
                        }
                    }
                }

            }
        }
    }
    private static void drawBatch(Batch batch, double x, double y, float scale){
        ImageAssets.draw(batch, A.images.pixel, (float) x, (float) y, 0.5f, 0.5f, scale, scale, 0);
    }

    private static void drawSr(ShapeRenderer renderer, double x, double y, float scale){
        renderer.point(((float) x), (float) y, 0);
    }

    private void renderWithSr(){
        sr.begin(ShapeRenderer.ShapeType.Line);

        double rightX = Utils.camRightX(cam);
        double leftX = Utils.camLeftX(cam);
        double botY = Utils.camBotY(cam);
        double topY = Utils.camTopY(cam);
        double step = cam.zoom;
        float scale = cam.zoom;


        for (Entity function : functions) {
            BiFunctionComponent bfc = function.get(M.biFun);
            sr.setColor(bfc.color);
            double resolution = bfc.resolutionMultiplier;
            double thickness = bfc.thickness;
            double valMinMax = step * thickness * resolution;

            for (double x = leftX; x < rightX; x += step * resolution) {
                for (double y = botY; y < topY; y += step * resolution) {
                    double val = bfc.fun.f(x, y);
                    if (val < valMinMax && val > -valMinMax){
                        sr.line((float) x, ((float) y), (float) x + scale * 2, ((float) y + scale * 2));
                        y += step * resolution * 2;
                    }
                }
            }
        }

        sr.end();
    }


    private static boolean plot(double x, double y, double precision){
        double left = sin(sin(x) + cos(y));
        double right = cos(sin(x * y) + cos(x));
        double v = left - right;
        return v > -precision && v < precision;
    }

    public static boolean test(double x, double y, double precision){
        return Math.abs((0.2 * pow(x - 70, 2)) + 0.8 * pow(y - 20, 2)) < precision;
    }

    public static boolean batmanEquation(double x, double y){
        if (pow(x, 2.0) / 49.0 + pow(y, 2.0) / 9.0 - 1.0 <= 0 && abs(x) >= 4.0 && -(3.0 * sqrt(33.0)) / 7.0 <= y && y <= 0) {
            return true;
        }
        if (pow(x, 2.0) / 49.0 + pow(y, 2.0) / 9.0 - 1.0 <= 0 && abs(x) >= 3.0 && -(3.0 * sqrt(33.0)) / 7.0 <= y && y >= 0) {
            return true;
        }
        if (-3.0 <= y
                && y <= 0
                && -4.0 <= x
                && x <= 4.0
                && (abs(x)) / 2.0 + sqrt(1.0 - pow(abs(abs(x) - 2.0) - 1.0, 2.0)) - 1.0 / 112.0 * (3.0 * sqrt(33.0) - 7.0)
                * pow(x, 2.0) - y - 3.0 <= 0) {
            return true;
        }

        if (y >= 0 && 3.0 / 4.0 <= abs(x) && abs(x) <= 1.0 && -8.0 * abs(x) - y + 9.0 >= 0) {
            return true;
        }

        if (1.0 / 2.0 <= abs(x) && abs(x) <= 3.0 / 4.0 && 3.0 * abs(x) - y + 3.0 / 4.0 >= 0 && y >= 0) {
            return true;
        }

        if (abs(x) <= 1.0 / 2.0 && y >= 0 && 9.0 / 4.0 - y >= 0) {
            return true;
        }

        if (abs(x) >= 1.0
                && y >= 0
                && -(abs(x)) / 2.0 - 3.0 / 7.0 * sqrt(10.0) * sqrt(4.0 - pow(abs(x) - 1.0, 2.0)) - y + (6.0 * sqrt(10.0)) / 7.0
                + 3.0 / 2.0 >= 0) {
            return true;
        }
        return false;
    }
}

package ru.maklas.melnikov.functions.bi_functions;

import ru.maklas.melnikov.utils.Utils;

import static ru.maklas.melnikov.utils.StringUtils.df;

public class SerovNashBiFunction extends GraphBiFunction {

    private final double x;
    private final double y;
    private final double xA;
    private final double yA;

    public SerovNashBiFunction(double x, double y, double xA, double yA) {
        this.x = x;
        this.y = y;
        this.xA = xA;
        this.yA = yA;
    }

    public static SerovNashBiFunction rand(double minValue, double maxValue) {
        double x = Utils.rand.nextDouble() * (maxValue - minValue) + minValue;
        double y = Utils.rand.nextDouble() * (maxValue - minValue) + minValue;
        double c = Utils.rand.nextDouble();
        double d = Utils.rand.nextDouble();
        return new SerovNashBiFunction(x, y, c, d);
    }

    @Override
    public double f(double x1, double y) {
        double left = x1 - x;
        double right = y - this.y;
        return xA * (left * left) + yA * (right * right);
    }

    @Override
    public double g(double x1, double y) {
        double dx = (2 * xA) * (x1 - x);
        double dy = (2 * yA) * (y - this.y);
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String name() {
        return df(xA, 1) + " * (x1 - " + df(x, 1) + ")^2 + " + df(yA, 1) + " * (x2 - " + df(y, 1) + ")^2";
    }
}

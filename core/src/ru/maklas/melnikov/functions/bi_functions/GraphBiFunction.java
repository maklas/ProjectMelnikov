package ru.maklas.melnikov.functions.bi_functions;

import ru.maklas.melnikov.functions.GraphFunction;

public abstract class GraphBiFunction {

    public abstract double f(double x1, double y);

    public double g(double x1, double y) {
        return f(x1, y);
    }

    public final double absF(double x1, double y){
        return Math.abs(f(x1, y));
    }

    public final GraphFunction lockX(double x){
        return y -> GraphBiFunction.this.f(x, y);
    }

    public final GraphFunction lockY(double y){
        return x -> GraphBiFunction.this.f(x, y);
    }

    public String name(){
        return "";
    }

}

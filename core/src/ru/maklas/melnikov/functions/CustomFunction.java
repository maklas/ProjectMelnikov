package ru.maklas.melnikov.functions;

public class CustomFunction implements GraphFunction {


    @Override
    public double f(double x) {
        return customSigmoid((x - 0.5));
    }


    public static double customSigmoid(double value){
        return 1 / (1 + Math.pow(1000, -value));
    }

    private static double threeWay(double x){
        return  1.0 / (1.0 - Math.pow(x, 2));
    }

    private static double squigly(double x){
        return  Math.cos(x) / x;
    }

}

package ru.maklas.melnikov.tests;


import com.badlogic.gdx.math.Vector2;
import ru.maklas.melnikov.mnw.MNW;
import ru.maklas.melnikov.utils.ClassUtils;
import ru.maklas.melnikov.utils.math.DoubleArray;
import ru.maklas.melnikov.utils.math.Matrix;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {

    public static void main(String[] args){
        Matrix a = new Matrix(new double[][]{{2, 3, 1}, {2, -7, 4}});
        Matrix b = new Matrix(new double[][]{{3, 4, 5}, {1, 1, 4}, {2, 1, 4}});
        System.out.println(a);
        System.out.println("+");
        System.out.println(b);
        System.out.println("=");
        System.out.println(a.mul(b));
		System.out.println();
        System.out.println(b.addColumn(DoubleArray.with(1.0, 2.0, 3.0), 0));
    }

    private static int f(int x){
        if (x == 1) return 0;
        return f(x - 1) + (x - 1);
    }

    private static void countStrings(){
        System.out.println(ClassUtils.countStrings(MNW.PACKAGE, false, false, false));
    }

    private static void testLerp(){
        Vector2 a = new Vector2();
        Vector2 b = new Vector2();
        Vector2 c = new Vector2();

        b.set(100, 100);
        for (int i = 0; i < 6; i++) {
            a.set(-100, -100);
            System.out.println(a.lerp(b, i * 0.2f));
        }
    }
}

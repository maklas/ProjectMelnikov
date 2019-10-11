package ru.maklas.melnikov.engine.functions;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.mengine.Component;
import ru.maklas.melnikov.functions.GraphFunction;

public class FunctionComponent implements Component {

    public GraphFunction graphFunction;
    public Color color = Color.WHITE.cpy();
    public boolean trackMouse = true;
    public double precision = 1d;
    public float lineWidth = 1f; //1..2
    public String name = "";

    public FunctionComponent(GraphFunction graphFunction) {
        this.graphFunction = graphFunction;
    }

    public FunctionComponent color(Color color){
        this.color = color;
        return this;
    }

}

package ru.maklas.melnikov.engine.input;

import ru.maklas.melnikov.engine.other.Event;

public class ScrollEvent implements Event {

    boolean up;

    public ScrollEvent(boolean up) {
        this.up = up;
    }

    public boolean zoomIn(){
        return up;
    }

    public boolean zoomOut(){
        return !up;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isDown(){
        return !up;
    }

}

package ru.maklas.melnikov.utils;

import ru.maklas.melnikov.engine.other.Dispatchable;
import ru.maklas.mengine.utils.EventDispatcher;

public class DebugEventDispatcher extends EventDispatcher {

    @Override
    public void dispatch(Object event) {
        if (!(event instanceof Dispatchable)) {
            Log.error(event.getClass().getSimpleName() + ".class does not implement Dispatchable interface");
        }
        super.dispatch(event);
    }
}

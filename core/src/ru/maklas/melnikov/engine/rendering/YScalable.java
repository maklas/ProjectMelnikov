package ru.maklas.melnikov.engine.rendering;

import ru.maklas.mengine.EntitySystem;

public interface YScalable<T extends EntitySystem> {

	T setYScale(double yScale);

}

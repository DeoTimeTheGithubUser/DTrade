package org.dtrade.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Couple<T> {
    boolean has(T t);
    void both(Consumer<T> action);
    T other(T t);

    default boolean meets(Predicate<T> pred) {
        AtomicBoolean met = new AtomicBoolean(false);
        both((t) -> {
            if(pred.test(t)) met.set(true);
        });
        return met.get();
    }
}

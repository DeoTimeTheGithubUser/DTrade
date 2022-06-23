package org.dtrade.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Couple<T> {

    T getFirst();
    T getSecond();

    boolean has(T t);
    void both(Consumer<T> action);
    T other(T t);

    default boolean oneMeets(Predicate<T> pred) {
        return pred.test(getFirst()) || pred.test(getSecond());
    }

    default boolean bothMeet(Predicate<T> pred) {
        return pred.test(getFirst()) && pred.test(getSecond());
    }
}

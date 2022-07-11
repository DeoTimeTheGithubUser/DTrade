package org.dtrade.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Couple<T> {

    T getFirst();
    T getSecond();

    default boolean has(T t) {
        return getFirst().equals(t) || getSecond().equals(t);
    }

    default void both(Consumer<T> action) {
        action.accept(getFirst());
        action.accept(getSecond());
    }

    default T other(T t) {
        return t.equals(getFirst()) ? getSecond() : getFirst();
    };

    default boolean oneMeets(Predicate<T> pred) {
        return pred.test(getFirst()) || pred.test(getSecond());
    }

    default boolean bothMeet(Predicate<T> pred) {
        return pred.test(getFirst()) && pred.test(getSecond());
    }

    default int indexOf(T t) {
        return !has(t) ? -1 : t.equals(getFirst()) ? 0 : 1;
    }

    static <T> Couple<T> of(T first, T second) {
        return new Couple<>() {
            @Override
            public T getFirst() {
                return first;
            }

            @Override
            public T getSecond() {
                return second;
            }
        };
    }
}

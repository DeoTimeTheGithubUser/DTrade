package org.dtrade.util;

import java.util.function.Consumer;

public interface Couple<T> {
    boolean has(T t);
    void both(Consumer<T> action);
    T other(T t);
}

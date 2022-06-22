package org.dtrade.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class ReflectUtils {

    @SneakyThrows
    public static void setField(Object object, String fieldName, Object value) {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

}

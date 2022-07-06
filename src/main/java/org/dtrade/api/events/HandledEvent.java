package org.dtrade.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

abstract class HandledEvent extends Event {
    private static final Map<Class<?>, HandlerList> handlers = new HashMap<>();

    private final HandlerList handler;

    protected HandledEvent() {
        this.handler = handlers.computeIfAbsent(this.getClass(), (c) -> new HandlerList());
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handler;
    }
}

package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.dtrade.logging.TradeLog;

// Called when the trade logger logs a trade
@RequiredArgsConstructor
public class TradeLogAddedEvent extends Event implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter @Setter
    private TradeLog log;

    private static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

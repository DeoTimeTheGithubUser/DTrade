package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.dtrade.trade.Trade;

// Called when a trade is completed
@RequiredArgsConstructor
public class TradeCompleteEvent extends Event implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter
    private final Trade trade;

    private static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

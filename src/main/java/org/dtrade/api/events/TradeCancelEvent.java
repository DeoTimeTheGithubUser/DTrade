package org.dtrade.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;

// Called when a trade has been cancelled
@RequiredArgsConstructor
public class TradeCancelEvent extends Event {
    @Getter
    private final Trade trade;
    @Getter
    private final Trader canceller;

    private static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

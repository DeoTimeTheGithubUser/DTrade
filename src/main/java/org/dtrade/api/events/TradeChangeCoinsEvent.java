package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.dtrade.trade.Trader;

// Called when a trader changes the amount of coins they have offered
@RequiredArgsConstructor
public class TradeChangeCoinsEvent extends Event implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter
    private final Trader trader;
    @NonNull @Getter @Setter
    private Long amount;

    private static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

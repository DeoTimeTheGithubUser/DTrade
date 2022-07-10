package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.dtrade.trade.Trader;

// Called when a trader adds an item to the trade
@RequiredArgsConstructor
public class TradeAddItemEvent extends Event implements Cancellable {


    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter
    private Trader trader;
    @NonNull @Getter @Setter
    private ItemStack item;


    private static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

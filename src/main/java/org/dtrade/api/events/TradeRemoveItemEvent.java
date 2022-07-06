package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.dtrade.trade.Trader;

// Called when a trader removes an item from the trade
@RequiredArgsConstructor
public class TradeRemoveItemEvent extends HandledEvent implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter
    private Trader trader;
    @NonNull @Getter @Setter
    private ItemStack item;
}

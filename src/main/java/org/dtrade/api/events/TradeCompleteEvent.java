package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.dtrade.trade.Trade;

// Called when a trade is completed
@RequiredArgsConstructor
public class TradeCompleteEvent extends HandledEvent implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter
    private final Trade trade;
}

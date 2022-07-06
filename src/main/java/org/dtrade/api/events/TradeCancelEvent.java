package org.dtrade.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;

// Called when a trade has been cancelled
@RequiredArgsConstructor
public class TradeCancelEvent extends HandledEvent {
    @Getter
    private final Trade trade;
    @Getter
    private final Trader canceller;
}

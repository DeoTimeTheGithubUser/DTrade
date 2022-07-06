package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.dtrade.trade.Trader;

// Called when a trader changes the amount of coins they have offered
@RequiredArgsConstructor
public class TradeChangeCoinsEvent extends HandledEvent implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter
    private final Trader trader;
    @NonNull @Getter @Setter
    private long amount;
}

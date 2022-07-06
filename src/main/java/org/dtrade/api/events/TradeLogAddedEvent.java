package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.dtrade.logging.TradeLog;

// Called when the trade logger logs a trade
@RequiredArgsConstructor
public class TradeLogAddedEvent extends HandledEvent implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter @Setter
    private TradeLog log;
}

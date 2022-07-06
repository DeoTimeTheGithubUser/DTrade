package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

// Called when a trade request is sent to a player
@RequiredArgsConstructor
public class TradeRequestEvent extends HandledEvent implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter
    private Player sender;
    @NonNull @Getter
    private Player receiver;
}

package org.dtrade.api.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// Called when a trade request is sent to a player
@RequiredArgsConstructor
public class TradeRequestEvent extends Event implements Cancellable {
    @Getter @Setter
    private boolean cancelled;
    @NonNull @Getter
    private Player sender;
    @NonNull @Getter
    private Player receiver;

    private static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

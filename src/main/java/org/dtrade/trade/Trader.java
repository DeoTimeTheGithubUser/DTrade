package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor @Data
public class Trader {

    private static final Set<Trader> TRADERS = new HashSet<>();

    private final Player player;
    private ItemStack[] offeredItems;
    private boolean acceptedTrade = false;

    public void toggleAccept() {
        acceptedTrade = !acceptedTrade;
    }

    // CALL THIS ON PLAYER LEAVE EVENT
    public void remove() {
        TRADERS.remove(this);
    }

    public static Trader createTrader(Player trader) {
        return new Trader(trader);
    }

    public static Trader getTrader(Player trader) {
        return TRADERS.stream().filter(t -> t.getPlayer().equals(trader)).findFirst().orElse(null);
    }


}

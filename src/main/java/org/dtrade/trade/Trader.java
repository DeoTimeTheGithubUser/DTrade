package org.dtrade.trade;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class Trader {

    private final Player player;
    private ItemStack[] offeredItems;
    private boolean acceptedTrade = false;

    public void toggleAccept() {
        acceptedTrade = !acceptedTrade;
    }

    public static Trader createTrader(Player trader) {
        return new Trader(trader);
    }


}

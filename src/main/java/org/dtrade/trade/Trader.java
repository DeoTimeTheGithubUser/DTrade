package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor @Data
public class Trader {

    private static final Set<Trader> TRADERS = new HashSet<>();

    private final Player player;
    private final List<ItemStack> offeredItems = new LinkedList<>();
    private boolean acceptedTrade = false;

    public void toggleAccept() {
        acceptedTrade = !acceptedTrade;
    }

    public void addTradeItem(ItemStack itemStack) {
        offeredItems.add(itemStack);
    }

    public void removeTradeItem(ItemStack itemStack) {
        offeredItems.remove(itemStack);
    }

    public static Trader createTrader(Player trader) {
        Trader trader1 = new Trader(trader);
        TRADERS.add(trader1);
        return trader1;
    }

    public static Trader getTrader(Player trader) {
        return TRADERS.stream().filter(t -> t.getPlayer().equals(trader)).findFirst().orElse(null);
    }


}

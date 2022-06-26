package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

@RequiredArgsConstructor @Data
public class Trader {

    private static final Set<Trader> TRADERS = new HashSet<>();

    private transient Trade trade;
    private final UUID traderID = UUID.randomUUID();

    private final Player player;
    private final List<ItemStack> offeredItems = new LinkedList<>();
    private long offeredCoins = 0;
    private boolean acceptedTrade = false;

    // TODO make this packet based (window ids arent working)
    private boolean toReopen = false;

    public void toggleAccept() {
        acceptedTrade = !acceptedTrade;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 100f, 1f);
        trade.getCouple().both((t) -> t.getPlayer().updateInventory());
        trade.updateTradeAccepted();
    }

    public void remove() {
        offeredItems.clear();
        TRADERS.remove(this);
        System.out.println("Removed trader " + traderID);
    }

    public void addTradeItem(ItemStack itemStack) {
        offeredItems.add(itemStack);
    }

    public void removeTradeItem(ItemStack itemStack) {
        offeredItems.remove(itemStack);
    }

    public static Trader createTrader(Player player) {
        Trader trader = new Trader(player);
        TRADERS.add(trader);
        System.out.println("Created trader " + trader.getTraderID());
        return trader;
    }

    public static Trader getTrader(Player trader) {
        return TRADERS.stream().filter(t -> t.getPlayer().equals(trader)).findFirst().orElse(null);
    }


}

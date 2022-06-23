package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor @Data
public class Trader {

    private static final Set<Trader> TRADERS = new HashSet<>();

    @NotNull
    private final Trade trade = ((Supplier<Trade>) () -> {
        Trade t = Trade.getTradeOf(this);
        if(t == null) {
            remove();
            throw new IllegalStateException("Trader has no trade.");
        }
        return t;
    }).get();

    private final Player player;
    private final List<ItemStack> offeredItems = new LinkedList<>();
    private boolean acceptedTrade = false;

    public void toggleAccept() {
        acceptedTrade = !acceptedTrade;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 100f, 1f);
        trade.getCouple().both((t) -> t.getPlayer().updateInventory());
        trade.updateTradeAccepted();
    }

    public void remove() {
        offeredItems.clear();
        TRADERS.remove(this);
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
        return trader;
    }

    public static Trader getTrader(Player trader) {
        return TRADERS.stream().filter(t -> t.getPlayer().equals(trader)).findFirst().orElse(null);
    }


}

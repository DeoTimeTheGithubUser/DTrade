package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.dtrade.gui.guis.TradeGui;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor @Data
public class Trade {

    private static final Set<Trade> trades = new HashSet<>();

    private final TradeCouple couple;

    private boolean cancelled;

    public void cancel(Trader canceller) {
        cancelled = true;
        couple.both((t) -> {
            t.getPlayer().sendMessage(t.equals(canceller) ? "\u00a7cYou cancelled the trade." : "\u00a7c" + canceller.getPlayer().getName() + " cancelled the trade.");
            if (!t.equals(canceller)) t.getPlayer().closeInventory();
            trades.remove(this);
        });
    }

    public void initializeTrade() {
        couple.both((t) -> {
            t.getPlayer().openInventory(new TradeGui(this));
            t.getPlayer().sendMessage("\u00a7aYou are now trading with " + couple.other(t).getPlayer().getName() + ".");
        });
    }

    public static Trade createTrade(TradeCouple couple) {
        Trade trade = new Trade(couple);
        trades.add(trade);
        return trade;
    }

    @SneakyThrows
    public static Trade getTradeOf(Trader trader) {
        Trade[] possibleTrades =  trades.stream()
                .filter(t -> t.getCouple().has(trader))
                .toArray(Trade[]::new);
        if(possibleTrades.length > 1) throw new MultiTradeException(trader);
        return possibleTrades.length == 0 ? null : possibleTrades[0];
    }

}

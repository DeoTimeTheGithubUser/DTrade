package org.dtrade.trade;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.BanList;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor @Data
public class Trade {

    private static final Set<Trade> trades = new HashSet<>();

    private final TradeCouple couple;

    public static Trade createTrade(TradeCouple couple) {
        Trade trade = new Trade(couple);
        trades.add(trade);
        return trade;
    }

    @SneakyThrows
    public static Trade getTradeOf(Trader trader) {
        Trade[] possibleTrades =  trades.stream()
                .filter(t -> t.getCouple().hasTrader(trader))
                .toArray(Trade[]::new);
        if(possibleTrades.length > 1) throw new MultiTradeException(trader);
        return possibleTrades.length == 0 ? null : possibleTrades[0];
    }

}

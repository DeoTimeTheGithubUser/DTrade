package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor @Data
public class TradeCouple {

    private final Trader trader1;
    private final Trader trader2;

    public boolean hasTrader(Trader trader) {
        return trader1.equals(trader) || trader2.equals(trader);
    }

    public void both(Consumer<Trader> action) {
        action.accept(trader1);
        action.accept(trader2);
    }

    public static TradeCouple of(Trader trader1, Trader trader2) {
        return new TradeCouple(trader1, trader2);
    }

    public Trader other(Trader trader) {
        return trader.equals(trader1) ? trader2 : trader1;
    }

}

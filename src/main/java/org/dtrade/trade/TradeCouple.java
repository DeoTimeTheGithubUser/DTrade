package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dtrade.util.Couple;

import java.util.function.Consumer;

@RequiredArgsConstructor @Data
public class TradeCouple implements Couple<Trader> {

    private final Trader trader1;
    private final Trader trader2;

    @Override
    public boolean has(Trader trader) {
        return trader1.equals(trader) || trader2.equals(trader);
    }

    @Override
    public void both(Consumer<Trader> action) {
        action.accept(trader1);
        action.accept(trader2);
    }

    @Override
    public Trader other(Trader trader) {
        return trader.equals(trader1) ? trader2 : trader1;
    }

    public static TradeCouple of(Trader trader1, Trader trader2) {
        return new TradeCouple(trader1, trader2);
    }


}

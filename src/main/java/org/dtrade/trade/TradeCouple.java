package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dtrade.util.Couple;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class TradeCouple implements Couple<Trader> {

    private final Trader trader1;
    private final Trader trader2;

    @Override
    public Trader getFirst() {
        return trader1;
    }

    @Override
    public Trader getSecond() {
        return trader2;
    }

    public static TradeCouple of(Trader trader1, Trader trader2) {
        return new TradeCouple(trader1, trader2);
    }


}

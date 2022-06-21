package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Data
public class Trade {

    private final TradeCouple couple;

    public static Trade createTrade(TradeCouple couple) {
        return new Trade(couple);
    }

}

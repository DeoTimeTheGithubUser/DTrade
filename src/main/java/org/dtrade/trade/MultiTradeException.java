package org.dtrade.trade;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;

public class MultiTradeException extends Exception{
    public MultiTradeException(Trader trader, Trade... trades) {
        super(createMessage(trader, trades));
    }

    private static String createMessage(Trader trader, Trade... trades) {
        return String.format("Player \"%s\" is in %s trades: %s",
                trader.getPlayer().getName(),
                trades.length,
                Arrays.toString(Arrays.stream(trades).map(Trade::getTradeID).toArray(UUID[]::new)));
    }
}

package org.dtrade.trade;

public class MultiTradeException extends Exception{
    public MultiTradeException(Trader trader) {
        super("Player \"" + trader.getPlayer().getName() + "\" is in multiple trades.");
    }
}

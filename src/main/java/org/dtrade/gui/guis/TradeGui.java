package org.dtrade.gui.guis;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.dtrade.gui.management.Gui;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;


public class TradeGui extends Gui {

    private final Trade trade;

    private static String NAME;
    private static final int SIZE = 54;

    public TradeGui(Trade trade) {
        super(NAME, SIZE);
        this.trade = trade;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if(isOtherTraderSlot(event.getSlot())) {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onClose(InventoryCloseEvent close) {
        trade.cancel(Trader.getTrader((Player) close.getPlayer()));
    }

    private static boolean isOtherTraderSlot(int slot) {
        return slot % 9 > 4;
    }

}

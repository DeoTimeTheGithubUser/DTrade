package org.dtrade.gui.guis;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.dtrade.gui.management.Gui;
import org.dtrade.gui.management.GuiUtils;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;


public class TradeGui extends Gui {

    private final Trade trade;

    private static String NAME = "Trade";
    private static final int SIZE = 54;

    public TradeGui(Trade trade) {
        super(NAME, SIZE);
        this.trade = trade;

        for (int i = 0; i < SIZE; i++) {
            if (i % 9 == 4) this.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

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
        if (!trade.isCancelled()) trade.cancel(Trader.getTrader((Player) close.getPlayer()));
    }

    private static boolean isOtherTraderSlot(int slot) {
        return slot % 9 >= 4;
    }

}

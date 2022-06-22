package org.dtrade.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.dtrade.gui.management.Gui;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.TradeUtils;


public class TradeGui extends Gui {

    private final Trade trade;

    private static String NAME = "Trade";
    private static final int SIZE = 54;

    public TradeGui(Trader trader) {
        super(NAME, SIZE);
        this.trade = Trade.getTradeOf(trader);

        for (int i = 0; i < SIZE; i++) {
            if (i % 9 == 4) this.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Trader trader = Trader.getTrader((Player) event.getWhoClicked());
        int slot = event.getSlot();
        if(TradeUtils.isOtherTraderSlot(event.getSlot()) || TradeUtils.isMiddle(event.getSlot())) return;
        if(event.getClickedInventory() == null) return;
        if (event.getClickedInventory().equals(trader.getPlayer().getInventory())) {
            ItemStack offeredItem = event.getCurrentItem();
            if(offeredItem == null) return;
            trader.getPlayer().getInventory().setItem(slot, null);
            trader.addTradeItem(offeredItem);
        } else if (event.getClickedInventory().equals(this)){
            Bukkit.broadcastMessage(String.valueOf(TradeUtils.convertSlotToTradeIndex(slot)));
            if(trader.getOfferedItems().isEmpty() || trader.getOfferedItems().size() < slot) return;
            ItemStack removedItem = trader.getOfferedItems().get(slot);
            trader.removeTradeItem(removedItem);
            trader.getPlayer().getInventory().addItem(removedItem);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent close) {
        if (!trade.isCancelled()) trade.cancel(Trader.getTrader((Player) close.getPlayer()));
    }

}

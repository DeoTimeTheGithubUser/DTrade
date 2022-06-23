package org.dtrade.gui.guis;

import lombok.experimental.ExtensionMethod;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.dtrade.gui.management.Gui;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.ItemUtils;
import org.dtrade.util.TradeUtils;

@ExtensionMethod({ItemUtils.class})
public class TradeGui extends Gui {

    private final Trade trade;
    private final Trader trader;
    private final Trader otherTrader;

    public static final int SIZE = 54;

    public TradeGui(Trader trader) {
        super("Trading with " + trader.getTrade().getCouple().other(trader).getPlayer().getName(), SIZE);
        this.trade = trader.getTrade();
        this.trader = trader;
        this.otherTrader = trade.getCouple().other(trader);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getSlot();
        if(event.getClickedInventory() == null) return;
        if(slot == 49) {
            trader.toggleAccept();
            return;
        }
        if (event.getClickedInventory().equals(trader.getPlayer().getInventory())) {
            ItemStack offeredItem = event.getCurrentItem();
            if(offeredItem == null) return;
            trader.getPlayer().getInventory().setItem(slot, null);
            trader.addTradeItem(offeredItem);
            trade.getCouple().other(trader).getPlayer().updateInventory();
        } else if (event.getClickedInventory().equals(this)){
            if(TradeUtils.isOtherTraderSlot(event.getSlot()) || TradeUtils.isMiddle(event.getSlot())) return;
            if(trader.getOfferedItems().isEmpty() || trader.getOfferedItems().size() <= TradeUtils.convertSlotToTradeIndex(slot)) return;
            int converted = TradeUtils.convertSlotToTradeIndex(slot);
            ItemStack removedItem = trader.getOfferedItems().get(converted);
            trader.removeTradeItem(removedItem);
            trader.getPlayer().getInventory().addItem(removedItem);
            trade.getCouple().other(trader).getPlayer().updateInventory();
        }
        trader.getPlayer().updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if(trade.isCancelled()) event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent close) {
        if (!trade.isCancelled()) trade.cancel(Trader.getTrader((Player) close.getPlayer()));
    }

}

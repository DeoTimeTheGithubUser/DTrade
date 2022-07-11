package org.dtrade.gui.guis;

import lombok.experimental.ExtensionMethod;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.dtrade.api.events.TradeAddItemEvent;
import org.dtrade.api.events.TradeChangeCoinsEvent;
import org.dtrade.api.events.TradeRemoveItemEvent;
import org.dtrade.config.DTradeConfig;
import org.dtrade.gui.management.Gui;
import org.dtrade.packets.SignInput;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.Blacklisted;
import org.dtrade.util.ItemUtils;
import org.dtrade.util.TradeUtils;

@ExtensionMethod({ItemUtils.class})
public class GuiTrade extends Gui {

    private final Trade trade;
    private final Trader trader;

    public static final int SIZE = 54;

    public GuiTrade(Trader trader) {
        super(DTradeConfig.color(DTradeConfig.getTradeGuiTitle(), trader.getPartner().getPlayer()), SIZE);
        this.trade = trader.getTrade();
        this.trader = trader;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getSlot();
        if(event.getClickedInventory() == null) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(trade.getPlugin(), () -> trader.getPlayer().updateInventory(), 1L);
        if(slot == 49) {
            trader.toggleAccept();
            return;
        }
        if(slot == 40) {
            trade.getCouple().both(t -> t.setAcceptedTrade(false));
            String[] prompt = DTradeConfig.colorArr(DTradeConfig.getPrompt());
            SignInput.requestSignInput(trade.getPlugin(), trader.getPlayer(), prompt).thenAccept(lines -> {
                String input = lines[0];
                boolean valid;
                Long amount = null;
                try {
                    amount = Long.parseLong(input);
                    valid = true;
                } catch (NumberFormatException ex) {
                    valid = false;
                }
                if(valid && (amount < DTradeConfig.getMinCoinInput() || amount > DTradeConfig.getMaxCoinInput())) valid = false;


                int windowID = ((CraftPlayer) trader.getPlayer()).getHandle().bV.j;
                Containers<?> type = ((CraftPlayer) trader.getPlayer()).getHandle().bV.a();
                IChatBaseComponent title = new ChatMessage(trader.getPlayer().getOpenInventory().getTitle());
                PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(windowID, type, title);

                ((CraftPlayer) trader.getPlayer()).getHandle().b.a(packet);
                trader.getTrade().getCouple().both(t -> Bukkit.getScheduler().runTask(trade.getPlugin(), () -> t.getPlayer().updateInventory()));

                if(valid) {
                    if(!trader.hasCoins(amount)) trader.getPlayer().sendMessage(DTradeConfig.prefix(DTradeConfig.getNotEnoughCoins()));
                    else {
                        TradeChangeCoinsEvent coinEvent = new TradeChangeCoinsEvent(trader, amount);
                        Bukkit.getPluginManager().callEvent(coinEvent);
                        if(coinEvent.isCancelled()) return;
                        trader.setOfferedCoins(coinEvent.getAmount());
                    }
                }
                else trader.getPlayer().sendMessage(DTradeConfig.prefix(DTradeConfig.getInvalidCoins()));

            });
            return;
        }
        if (event.getClickedInventory().equals(trader.getPlayer().getInventory())) {
            ItemStack offeredItem = event.getCurrentItem();
            if(offeredItem == null) return;

            if(Blacklisted.isItemBlacklisted(offeredItem)) {
                trader.getPlayer().sendMessage(DTradeConfig.prefix(DTradeConfig.getBlacklistedMessage()));
                return;
            }

            TradeAddItemEvent addItemEvent = new TradeAddItemEvent(trader, offeredItem);
            Bukkit.getPluginManager().callEvent(addItemEvent);
            if(addItemEvent.isCancelled()) return;

            trade.getCouple().both(t -> t.setAcceptedTrade(false));
            trader.getPlayer().getInventory().setItem(slot, null);
            trader.addTradeItem(addItemEvent.getItem());
            trader.getPartner().getPlayer().updateInventory();
        } else if (event.getClickedInventory().equals(this)){
            if(TradeUtils.isOtherTraderSlot(event.getSlot()) || TradeUtils.isMiddle(event.getSlot())) return;
            if(trader.getOfferedItems().isEmpty() || trader.getOfferedItems().size() <= TradeUtils.convertSlotToTradeIndex(slot)) return;
            int converted = TradeUtils.convertSlotToTradeIndex(slot);
            trade.getCouple().both(t -> t.setAcceptedTrade(false));
            ItemStack removedItem = trader.getOfferedItems().get(converted);
            if(removedItem == null) return;

            TradeRemoveItemEvent removeItemEvent = new TradeRemoveItemEvent(trader, removedItem);
            Bukkit.getPluginManager().callEvent(removeItemEvent);
            if(removeItemEvent.isCancelled()) return;

            trader.removeTradeItem(converted);
            trader.getPlayer().getInventory().addItem(removeItemEvent.getItem().clone());
            trader.getPartner().getPlayer().updateInventory();
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if(trade.isCancelled()) event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!trade.isCancelled()) trade.cancel(Trader.getTrader((Player) event.getPlayer()));
    }

}

package org.dtrade.gui.guis;

import lombok.experimental.ExtensionMethod;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.dtrade.gui.management.Gui;
import org.dtrade.packets.SignInput;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.ItemUtils;
import org.dtrade.util.TradeUtils;

import java.util.Arrays;

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
        Bukkit.getScheduler().scheduleSyncDelayedTask(trade.getPlugin(), () -> trader.getPlayer().updateInventory(), 1L);
        if(slot == 49) {
            trader.toggleAccept();
            return;
        }
        if(slot == 40) {
            trade.getCouple().both(t -> t.setAcceptedTrade(false));
            SignInput.requestSignInput(trade.getPlugin(), trader.getPlayer(), new String[]{"", "^^^^^^^^^^^^^^", "Enter coins to", "offer in trade."}).thenAccept(lines -> {
                String input = lines[0];
                boolean valid;
                Long amount = null;
                try {
                    amount = Long.parseLong(input);
                    valid = true;
                } catch (NumberFormatException ex) {
                    valid = false;
                }
                if(valid && amount < 0) valid = false;

                int windowID = ((CraftPlayer) trader.getPlayer()).getHandle().bV.j;
                Containers<?> type = ((CraftPlayer) trader.getPlayer()).getHandle().bV.a();
                IChatBaseComponent title = new ChatMessage(trader.getPlayer().getOpenInventory().getTitle());
                PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(windowID, type, title);

                ((CraftPlayer) trader.getPlayer()).getHandle().b.a(packet);
                trader.getTrade().getCouple().both(t -> Bukkit.getScheduler().scheduleSyncDelayedTask(trade.getPlugin(), () -> t.getPlayer().updateInventory(), 1L));

                if(valid) {
                    if(!trader.hasCoins(amount)) trader.getPlayer().sendMessage("\u00a7cYou do not have enough coins!");
                    else trader.setOfferedCoins(amount);
                }
                else trader.getPlayer().sendMessage("\u00a7cInvalid coin input!");

            });
            return;
        }
        if (event.getClickedInventory().equals(trader.getPlayer().getInventory())) {
            ItemStack offeredItem = event.getCurrentItem();
            if(offeredItem == null) return;
            trade.getCouple().both(t -> t.setAcceptedTrade(false));
            trader.getPlayer().getInventory().setItem(slot, null);
            trader.addTradeItem(offeredItem);
            trade.getCouple().other(trader).getPlayer().updateInventory();
        } else if (event.getClickedInventory().equals(this)){
            if(TradeUtils.isOtherTraderSlot(event.getSlot()) || TradeUtils.isMiddle(event.getSlot())) return;
            if(trader.getOfferedItems().isEmpty() || trader.getOfferedItems().size() <= TradeUtils.convertSlotToTradeIndex(slot)) return;
            int converted = TradeUtils.convertSlotToTradeIndex(slot);
            trade.getCouple().both(t -> t.setAcceptedTrade(false));
            ItemStack removedItem = trader.getOfferedItems().get(converted);
            trader.removeTradeItem(removedItem);
            trader.getPlayer().getInventory().addItem(removedItem.clone());
            trade.getCouple().other(trader).getPlayer().updateInventory();
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if(trade.isCancelled()) event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Trader trader = Trader.getTrader((Player) event.getPlayer());
        if (!trade.isCancelled()) trade.cancel(Trader.getTrader((Player) event.getPlayer()));
    }

}

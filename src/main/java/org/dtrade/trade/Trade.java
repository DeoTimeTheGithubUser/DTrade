package org.dtrade.trade;

import lombok.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dtrade.DTrade;
import org.dtrade.EconomyHandler;
import org.dtrade.api.events.TradeCancelEvent;
import org.dtrade.api.events.TradeCompleteEvent;
import org.dtrade.config.DTradeConfig;
import org.dtrade.gui.guis.GuiTrade;
import org.dtrade.logging.TradeLog;
import org.dtrade.logging.TradeLogger;
import org.dtrade.util.ItemUtils;
import org.dtrade.util.TradeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class Trade {

    private static final Set<Trade> trades = new HashSet<>();

    private final UUID tradeID = UUID.randomUUID();

    private final DTrade plugin;
    private final TradeCouple couple;

    private boolean cancelled;

    private int secondsUntilAccept = -1;
    private BukkitRunnable timer;

    public Trade(DTrade plugin, TradeCouple couple) {
        this.plugin = plugin;
        this.couple = couple;
    }

    public void cancel(Trader canceller) {
        TradeCancelEvent cancelEvent = new TradeCancelEvent(this, canceller);
        Bukkit.getPluginManager().callEvent(cancelEvent);
        cancelled = true;
        trades.removeIf(t -> t.getCouple().equals(couple));
        couple.both(t -> {
            Player player = t.getPlayer();
            List<ItemStack> offeredItems = t.getOfferedItems();

            ItemUtils.addToInventoryOrDrop(plugin, player, offeredItems.toArray(ItemStack[]::new),
                    (i) -> player.sendMessage(DTradeConfig.prefix(DTradeConfig.getItemDroppedBecauseNoSpace())));

            t.remove();
            String cancelMsg = canceller == null ? DTradeConfig.prefix("\u00a7cSomething went wrong with your trade!") : t.equals(canceller) ? DTradeConfig.prefix(DTradeConfig.getCancelledTrade()) : DTradeConfig.prefix(DTradeConfig.getOtherCancelledTrade(), t.getPartner().getPlayer());
            t.getPlayer().sendMessage(cancelMsg);
            if (!t.equals(canceller)) t.getPlayer().closeInventory();
        });
    }

    public void initializeTrade() {
        couple.both((t) -> {
            t.getPlayer().openInventory(new GuiTrade(t));
            t.getPlayer().sendMessage(DTradeConfig.prefix(DTradeConfig.getNowTradingWith(), t.getPartner().getPlayer()));
        });
    }

    public static @NotNull Trade createTrade(DTrade plugin, TradeCouple couple) {
        Trade trade = new Trade(plugin, couple);
        trades.add(trade);
        couple.both(t -> t.setTrade(trade));
        return trade;
    }

    public boolean isTradeAccepted() {
        return couple.bothMeet(Trader::isAcceptedTrade);
    }

    public void updateTradeAccepted() {
        if (!isTradeAccepted()) return;

        secondsUntilAccept = DTradeConfig.getSecondsUntilAccept();

        if(timer != null) timer.cancel();
        timer = new BukkitRunnable() {

            @Override
            public void run() {

                if(Trade.this.isCancelled()) {
                    this.cancel();
                    return;
                }

                if (!isTradeAccepted()) {
                    secondsUntilAccept = -1;
                    couple.both(t -> Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> t.getPlayer().updateInventory(), 1L));
                    this.cancel();
                    return;
                }

                couple.both(t -> Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> t.getPlayer().updateInventory(), 1L));

                if (secondsUntilAccept == 0) {
                    confirmTrade();
                    this.cancel();
                    secondsUntilAccept = -1;
                    couple.both(t -> t.getPlayer().playSound(t.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 1f));
                    return;
                } else couple.both(t -> t.getPlayer().playSound(t.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100f, 1f));

                secondsUntilAccept--;

            }

        };
        timer.runTaskTimer(plugin, 0L, 20L);

    }

    private void confirmTrade() {
        Bukkit.broadcastMessage("I AM TRIGGERED! GLITCH");

        TradeCompleteEvent completeEvent = new TradeCompleteEvent(this);
        Bukkit.getPluginManager().callEvent(completeEvent);
        if(completeEvent.isCancelled()) {
            this.secondsUntilAccept = -1;
            couple.both(t -> t.setAcceptedTrade(false));
            return;
        }

        Economy eco = plugin.getEconomyHandler().getEconomy();

        if(!couple.bothMeet(t -> t.hasCoins(t.getOfferedCoins()))) {
            cancel(null);
            return;
        }

        cancelled = true;
        couple.both(t -> {
            Trader partner = t.getPartner();

            long receivedCoins = partner.getOfferedCoins();
            long offeredCoins = t.getOfferedCoins();

            t.getPlayer().playSound(t.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 1f);
            if(DTradeConfig.isReceiptEnabled()) t.getPlayer().spigot().sendMessage(TradeUtils.getTradeReceipt(t));

            ItemUtils.addToInventoryOrDrop(plugin, partner.getPlayer(), t.getOfferedItems().toArray(ItemStack[]::new),
                    (i) -> partner.getPlayer().sendMessage(DTradeConfig.prefix(DTradeConfig.getItemDroppedBecauseNoSpace())));
            eco.depositPlayer(t.getPlayer(), receivedCoins);
            eco.withdrawPlayer(t.getPlayer(), offeredCoins);
            t.setTrade(null);
            t.remove();
        });
        couple.both(t -> t.getPlayer().closeInventory());
        trades.removeIf(t -> t.getCouple().equals(couple));
        plugin.getTradeLogger().log(TradeLog.createLog(this));
    }

}

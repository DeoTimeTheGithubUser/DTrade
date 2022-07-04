package org.dtrade.trade;

import lombok.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dtrade.DTrade;
import org.dtrade.EconomyHandler;
import org.dtrade.gui.guis.TradeGui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
public class Trade {

    private static final Set<Trade> trades = new HashSet<>();

    private final UUID tradeID = UUID.randomUUID();

    private final Plugin plugin;
    private final TradeCouple couple;

    private boolean cancelled;

    private int secondsUntilAccept = -1;

    public Trade(Plugin plugin, TradeCouple couple) {
        this.plugin = plugin;
        this.couple = couple;
    }

    public void cancel(@NotNull Trader canceller) {
        cancelled = true;
        trades.removeIf(t -> t.getTradeID().equals(tradeID));
        couple.both(t -> {
            Player player = t.getPlayer();
            List<ItemStack> offeredItems = t.getOfferedItems();

            for(ItemStack item : offeredItems) {
                if(player.getInventory().firstEmpty() != -1) player.getInventory().addItem(item);
                else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    player.sendMessage("\u00a7cYour inventory was full so an item was dropped.");
                }
            }

            t.remove();
            t.getPlayer().sendMessage(t.equals(canceller) ? "\u00a7cYou cancelled the trade." : "\u00a7c" + canceller.getPlayer().getName() + " cancelled the trade.");
            if (!t.equals(canceller)) t.getPlayer().closeInventory();
        });
    }

    public void initializeTrade() {
        couple.both((t) -> {
            t.getPlayer().openInventory(new TradeGui(t));
            t.getPlayer().sendMessage("\u00a7aYou are now trading with " + couple.other(t).getPlayer().getName() + ".");
        });
    }

    public static @NotNull Trade createTrade(Plugin plugin, TradeCouple couple) {
        Trade trade = new Trade(plugin, couple);
        trades.add(trade);
        couple.both(t -> t.setTrade(trade));
        return trade;
    }


    @SneakyThrows
    public static Trade getTradeOf(Trader trader) {
        Trade[] possibleTrades =  trades.stream()
                .filter(t -> t.getCouple().has(trader))
                .toArray(Trade[]::new);
        if(possibleTrades.length > 1) throw new MultiTradeException(trader, possibleTrades);
        return possibleTrades.length == 0 ? null : possibleTrades[0];
    }

    public boolean isTradeAccepted() {
        return couple.bothMeet(Trader::isAcceptedTrade);
    }

    public void updateTradeAccepted() {
        if (!isTradeAccepted()) return;

        secondsUntilAccept = 5;

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!isTradeAccepted()) {
                    secondsUntilAccept = -1;
                    couple.both(t -> {
                        t.getPlayer().updateInventory();
                    });
                    this.cancel();
                    return;
                }

                couple.both(t -> {
                    t.getPlayer().updateInventory();
                });

                if (secondsUntilAccept == 0) {
                    confirmTrade();
                    this.cancel();
                    secondsUntilAccept = -1;
                    return;
                }

                secondsUntilAccept--;

            }

        }.runTaskTimer(plugin, 0L, 20L);

    }

    private void confirmTrade() {

        class Interrupt {
            @Getter
            private String reason;
            @Getter
            private boolean interrupted = false;

            public void setReason(String reason) {
                interrupted = true;
                this.reason = reason;
            }

            public void send(Player player) {
                player.sendMessage("\u00a7c" + reason);
            }
        }

        cancelled = true;
        Interrupt interrupt = new Interrupt();
        Economy eco = EconomyHandler.getEconomyHandler().getEconomy();
        couple.both(t -> {
            if(interrupt.isInterrupted()) {
                interrupt.send(t.getPlayer());
                return;
            }
            Trader partner = couple.other(t);
            t.getPlayer().closeInventory();
            if(!t.hasCoins(t.getOfferedCoins())) {
                interrupt.setReason("Not enough coins.");
                interrupt.send(t.getPlayer());
                return;
            }

            long receivedCoins = partner.getOfferedCoins();

            t.getPlayer().playSound(t.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 1f);
            t.getPlayer().sendMessage("\u00a7aCompleted trade with " + partner.getPlayer().getName() + "!");
            if(receivedCoins != 0) t.getPlayer().sendMessage("\u00a7aYou received \u00a7e" + receivedCoins + "\u00a7a!");
            partner.getPlayer().getInventory().addItem(t.getOfferedItems().toArray(ItemStack[]::new));
            eco.depositPlayer(partner.getPlayer(), receivedCoins);
            t.remove();
        });
    }





}

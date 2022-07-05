package org.dtrade.trade;

import lombok.*;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dtrade.EconomyHandler;
import org.dtrade.gui.guis.TradeGui;
import org.dtrade.util.TradeUtils;
import org.jetbrains.annotations.NotNull;

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
        trades.removeIf(t -> t.getCouple().equals(couple));
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
            long offeredCoins = t.getOfferedCoins();

            t.getPlayer().playSound(t.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 1f);

            TextComponent complete = new TextComponent("\u00a77Completed trade with \u00a7a" + partner.getPlayer().getName() + "\u00a77!\n");

            String tradedReceipt = "\u00a77[\u00a7aTraded Receipt\u00a77]\u00a7a";
            TextComponent tComponent = new TextComponent(tradedReceipt);
            tComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TradeUtils.getTradeReceipt(t, false))));

            String receivedReceipt = "\u00a77[\u00a7aReceived Receipt\u00a77]\u00a7a";
            TextComponent rComponent = new TextComponent(receivedReceipt);
            rComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TradeUtils.getTradeReceipt(t, true))));

            t.getPlayer().spigot().sendMessage(complete, tComponent, new TextComponent("\u00a78 | "), rComponent);

            partner.getPlayer().getInventory().addItem(t.getOfferedItems().toArray(ItemStack[]::new));
            eco.depositPlayer(t.getPlayer(), receivedCoins);
            eco.withdrawPlayer(t.getPlayer(), offeredCoins);
            t.setTrade(null);
            t.remove();
        });
        trades.removeIf(t -> t.getCouple().equals(couple));
    }

}

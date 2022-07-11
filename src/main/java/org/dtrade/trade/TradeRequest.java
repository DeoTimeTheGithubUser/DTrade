package org.dtrade.trade;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dtrade.api.events.TradeRequestEvent;
import org.dtrade.config.DTradeConfig;
import org.dtrade.util.ChatUtils;

import java.util.HashMap;
import java.util.UUID;

public class TradeRequest {

    private static HashMap<UUID, UUID> requests = new HashMap<>();
    public static void createTradeRequest(Plugin plugin, Player initializer, Player requested) {

        if (requests.containsKey(initializer.getUniqueId())) {
            initializer.sendMessage(DTradeConfig.prefix(DTradeConfig.getAlreadyOutgoingRequest()));
            return;
        }

        if (requests.containsKey(requested.getUniqueId())) {
            Trade trade = Trade.createTrade(plugin, TradeCouple.of(Trader.createTrader(initializer), Trader.createTrader(requested)));
            trade.initializeTrade();
            requests.remove(requested.getUniqueId());
            return;
        }

        TradeRequestEvent requestEvent = new TradeRequestEvent(initializer, requested);
        Bukkit.getPluginManager().callEvent(requestEvent);
        if(requestEvent.isCancelled()) return;
        requests.put(initializer.getUniqueId(), requested.getUniqueId());
        initializer.sendMessage(DTradeConfig.prefix(DTradeConfig.getSentRequestTo(), requested));

        String sendMessage = DTradeConfig.prefix(DTradeConfig.getReceivedRequestFrom(), initializer);
        TextComponent component = ChatUtils.createCommandMessage(sendMessage, "/trade " + initializer.getName());
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("\u00a7aClick to accept trade!")));
        requested.spigot().sendMessage(component);
    }

}

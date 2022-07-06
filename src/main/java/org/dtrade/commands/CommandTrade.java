package org.dtrade.commands;

import com.sun.jna.platform.win32.WinNT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.dtrade.DTrade;
import org.dtrade.api.events.TradeRequestEvent;
import org.dtrade.trade.Trade;
import org.dtrade.trade.TradeCouple;
import org.dtrade.trade.Trader;
import org.dtrade.util.ChatUtils;
import org.dtrade.util.ItemUtils;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class CommandTrade implements CommandExecutor {

    private HashMap<UUID, UUID> requests = new HashMap<>();

    private final Plugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("\u00a7cPlease enter a username to trade.");
            sender.sendMessage(ItemUtils.getRealName(((Player)sender).getItemInHand()));
            return true;
        }

        Player player = (Player) sender;
        Player requested = Bukkit.getPlayer(args[0]);

        if (requested == null) {
            sender.sendMessage("\u00a7cIncorrect usage. /trade <player>");
            return true;
        }

        if (player.getUniqueId().equals(requested.getUniqueId())) {
            sender.sendMessage("\u00a7cYou cannot trade yourself.");
            return true;
        }

        if (requests.containsKey(player.getUniqueId())) {
            sender.sendMessage("\u00a7cYou already have an outgoing request.");
            return true;
        }

        if (requests.containsKey(requested.getUniqueId())) {
            Trade trade = Trade.createTrade(plugin, TradeCouple.of(Trader.createTrader(player), Trader.createTrader(requested)));
            trade.initializeTrade();
            requests.remove(requested.getUniqueId());
            return true;
        }

        TradeRequestEvent requestEvent = new TradeRequestEvent(player, requested);
        Bukkit.getPluginManager().callEvent(requestEvent);
        if(requestEvent.isCancelled()) return true;
        requests.put(player.getUniqueId(), requested.getUniqueId());
        player.sendMessage("\u00a7aYou sent a trade request to " + args[0] + ".");

        String sendMessage = "\u00a7aYou received a trade request from " + sender.getName() + ".";
        TextComponent component = ChatUtils.createCommandMessage(sendMessage, "/trade " + player.getName());
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("\u00a7aClick to accept trade!")));
        requested.spigot().sendMessage(component);

        return true;
    }

}

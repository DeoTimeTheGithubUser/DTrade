package org.dtrade.commands;

import com.sun.jna.platform.win32.WinNT;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.dtrade.DTrade;
import org.dtrade.trade.Trade;
import org.dtrade.trade.TradeCouple;
import org.dtrade.trade.Trader;
import org.dtrade.util.ChatUtils;

import java.util.HashMap;
import java.util.UUID;

public class CommandTrade implements CommandExecutor, Listener {

    private HashMap<UUID, UUID> requests = new HashMap<>();

    public CommandTrade() {
        DTrade.getInstance().getServer().getPluginManager().registerEvents(this, DTrade.getInstance());
        DTrade.getInstance().getCommand("trade").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("\u00a7cPlease enter a username to trade.");
            return true;
        }

        Player player = (Player) sender;
        Player requested = Bukkit.getPlayer(args[0]);

        if (requested == null) {
            sender.sendMessage("\u00a7cPlease enter a valid player to trade.");
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

            Trade trade = Trade.createTrade(TradeCouple.of(Trader.createTrader(player), Trader.createTrader(requested)));

            trade.initializeTrade();
            requests.remove(requested.getUniqueId());

            return true;
        }

        requests.put(player.getUniqueId(), requested.getUniqueId());
        player.sendMessage("\u00a7aYou sent a trade request to " + args[0] + ".");

        String sendMessage = "\u00a7aYou recieved a trade request from " + sender.getName() + ".";
        TextComponent component = ChatUtils.createCommandMessage(sendMessage, "/trade " + requested.getName());
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("\u00a7aClick to accept trade!")));
        requested.sendMessage(sendMessage);

        return true;
    }

}

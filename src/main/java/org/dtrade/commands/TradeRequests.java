package org.dtrade.commands;

import com.sun.jna.platform.win32.WinNT;
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

import java.util.HashMap;

public class TradeRequests implements CommandExecutor, Listener {

    private HashMap<Player, Player> requests = new HashMap<Player, Player>();

    public TradeRequests() {
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

        if (requests.containsKey(player)) {
            sender.sendMessage("\u00a7cYou already have an outgoing request.");
            return true;
        }

        if (requests.containsKey(requested)) {

            Trade trade = Trade.createTrade(TradeCouple.of(Trader.createTrader(player), Trader.createTrader(requested)));

            trade.initializeTrade();
            requests.remove(requested);

            return true;
        }

        requests.put(player, requested);
        player.sendMessage("\u00a7aYou sent a trade request to " + args[0] + ".");
        requested.sendMessage("\u00a7aYou recieved a trade request from " + sender.getName() + "."); // hover command todo

        return true;
    }

}

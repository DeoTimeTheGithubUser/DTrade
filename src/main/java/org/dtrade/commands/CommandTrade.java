package org.dtrade.commands;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dtrade.api.events.TradeRequestEvent;
import org.dtrade.config.DTradeConfig;
import org.dtrade.trade.Trade;
import org.dtrade.trade.TradeCouple;
import org.dtrade.trade.TradeRequest;
import org.dtrade.trade.Trader;
import org.dtrade.util.ChatUtils;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class CommandTrade implements CommandExecutor {

    private final Plugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(DTradeConfig.prefix(DTradeConfig.getNoUsernameEntered()));
            return true;
        }
        Player player = (Player) sender;
        Player requested = Bukkit.getPlayerExact(args[0]);
        if (requested == null) {
            sender.sendMessage(DTradeConfig.prefix(DTradeConfig.getPlayerRequestedNotExist()));
            return true;
        }
        TradeRequest.createTradeRequest(plugin, player, requested);
        return true;
    }

}

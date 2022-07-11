package org.dtrade.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dtrade.DTrade;
import org.dtrade.config.DTradeConfig;
import org.dtrade.trade.TradeRequest;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class CommandTrade implements CommandExecutor {

    private final DTrade plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
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

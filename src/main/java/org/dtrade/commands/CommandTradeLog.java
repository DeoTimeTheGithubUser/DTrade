package org.dtrade.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dtrade.config.DTradeConfig;
import org.dtrade.gui.guis.GuiTradeLogs;
import org.jetbrains.annotations.NotNull;

public class CommandTradeLog implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!commandSender.hasPermission("dtrade.tradelog") || !(commandSender instanceof Player)) {
            commandSender.sendMessage(DTradeConfig.prefix(DTradeConfig.getNoPermission()));
            return true;
        }
        if(strings.length < 1) {
            commandSender.sendMessage(DTradeConfig.prefix(DTradeConfig.getNoUsernameEntered()));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(strings[0]);
        ((Player) commandSender).openInventory(new GuiTradeLogs(target));
        return true;

    }
}

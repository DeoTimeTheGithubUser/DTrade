package org.dtrade.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dtrade.DTrade;
import org.dtrade.config.DTradeConfig;
import org.dtrade.gui.guis.GuiTradeLogs;
import org.dtrade.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@RequiredArgsConstructor
public class CommandTradeLog implements CommandExecutor {

    private final DTrade plugin;

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

        if(!Utils.hasPlayerJoinedBefore(strings[0])) {
            commandSender.sendMessage(DTradeConfig.prefix(DTradeConfig.getPlayerRequestedNotExist()));
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(strings[0]);
        ((Player) commandSender).openInventory(new GuiTradeLogs(plugin, target));
        return true;

    }
}

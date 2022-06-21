package org.dtrade.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.dtrade.DTrade;
import org.dtrade.gui.guis.CompareGui;

public class CompareMenu implements CommandExecutor, Listener {

    public CompareMenu() {
        DTrade.getInstance().getServer().getPluginManager().registerEvents(this, DTrade.getInstance());
        DTrade.getInstance().getCommand("compare").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ((Player)sender).openInventory(new CompareGui());
        ((CraftPlayer)sender).
        return true;
    }
}

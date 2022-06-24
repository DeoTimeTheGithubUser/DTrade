package org.dtrade;

import org.bukkit.plugin.java.JavaPlugin;
import org.dtrade.commands.CommandTrade;
import org.dtrade.gui.management.Gui;
import org.dtrade.gui.management.GuiManager;
import org.dtrade.packets.TradeView;

public class DTrade extends JavaPlugin {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new GuiManager(), this);
        getServer().getPluginManager().registerEvents(new TradeView(), this);

        getCommand("trade").setExecutor(new CommandTrade(this));

    }

}

package org.dtrade;

import org.bukkit.plugin.java.JavaPlugin;
import org.dtrade.commands.CommandTrade;
import org.dtrade.gui.management.GuiManager;
import org.dtrade.packets.TradeView;

public class DTrade extends JavaPlugin {

    static DTrade instance;

    public DTrade() {
        instance = this;
    }

    @Override
    public void onEnable() {

        new CommandTrade();
        new GuiManager();
        getServer().getPluginManager().registerEvents(new TradeView(), DTrade.getInstance());

    }

    public static DTrade getInstance() {
        return instance;
    }

}

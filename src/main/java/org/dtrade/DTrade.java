package org.dtrade;

import org.bukkit.plugin.java.JavaPlugin;
import org.dtrade.commands.CommandTrade;
import org.dtrade.gui.management.GuiManager;
import org.dtrade.packets.PacketHandler;
import org.dtrade.packets.TradeView;
import org.dtrade.util.Utils;

import java.sql.PreparedStatement;
import java.util.logging.Logger;

public class DTrade extends JavaPlugin {

    @Override
    public void onEnable() {


        PacketHandler.init(this);
        EconomyHandler.init(this);
        TradeView.init();

        getServer().getPluginManager().registerEvents(new GuiManager(), this);

        getCommand("trade").setExecutor(new CommandTrade(this));

    }

}

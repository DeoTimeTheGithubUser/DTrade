package org.dtrade;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.dtrade.commands.CommandTrade;
import org.dtrade.commands.CommandTradeLog;
import org.dtrade.config.Config;
import org.dtrade.config.DTradeConfig;
import org.dtrade.gui.management.GuiManager;
import org.dtrade.logging.TradeLogger;
import org.dtrade.packets.PacketHandler;
import org.dtrade.packets.TradeView;

import java.io.File;

// TODO multi-version support

public class DTrade extends JavaPlugin {

    @Getter
    private File loggerFile;

    @Override
    public void onEnable() {


        loggerFile = new File(this.getDataFolder(), "logs.json");

        Config.load(this, DTradeConfig.class);
        PacketHandler.init(this);
        Bukkit.getOnlinePlayers().forEach(PacketHandler.getPacketHandler()::registerPlayer);
        EconomyHandler.init(this);
        TradeLogger.init(this);
        TradeView.init();

        getServer().getPluginManager().registerEvents(new GuiManager(), this);

        getCommand("trade").setExecutor(new CommandTrade(this));
        getCommand("tradelog").setExecutor(new CommandTradeLog());

    }



    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().b.a.m.pipeline().remove("dpackethandler"));
    }

}

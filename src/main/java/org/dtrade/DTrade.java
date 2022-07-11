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
import org.dtrade.util.ListenerShiftRightClick;

import java.io.File;

// TODO multi-version support

public class DTrade extends JavaPlugin {

    @Getter
    private File loggerFile;
    @Getter
    private TradeLogger tradeLogger;
    @Getter
    private EconomyHandler economyHandler;
    @Getter
    private PacketHandler packetHandler;


    @Override
    public void onEnable() {


        loggerFile = new File(this.getDataFolder(), "logs.json");

        Config.load(this, DTradeConfig.class);
        this.packetHandler = new PacketHandler(this);
        this.economyHandler = new EconomyHandler(this);
        this.tradeLogger = new TradeLogger(this);
        TradeView.init(this);

        getServer().getPluginManager().registerEvents(new GuiManager(), this);
        getServer().getPluginManager().registerEvents(new ListenerShiftRightClick(this), this);

        getCommand("trade").setExecutor(new CommandTrade(this));
        getCommand("tradelog").setExecutor(new CommandTradeLog(this));

    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().b.a.m.pipeline().remove("dpackethandler"));
    }


}

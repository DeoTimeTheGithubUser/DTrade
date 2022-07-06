package org.dtrade.logging;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.gametest.framework.GameTest;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.dtrade.DTrade;
import org.dtrade.api.events.TradeLogAddedEvent;
import org.dtrade.util.Couple;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class TradeLogger {
    @Getter
    private static TradeLogger logger;

    private final DTrade plugin;
    private final Set<TradeLog> logs;

    @SneakyThrows
    private TradeLogger(@NotNull DTrade plugin) {
        if(!plugin.getLoggerFile().exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.getLoggerFile().createNewFile();
        }
        this.plugin = plugin;
        this.logs = new HashSet<>();
        loadLogs();
    }

    public void log(TradeLog log) {

        TradeLogAddedEvent logAddedEvent = new TradeLogAddedEvent(log);
        Bukkit.getPluginManager().callEvent(logAddedEvent);
        if(logAddedEvent.isCancelled()) return;

        this.logs.add(log);
        save();
    }

    @SneakyThrows
    public void save() {
        try(FileWriter writer = new FileWriter(plugin.getLoggerFile())) {
            String data = new Gson().toJson(logs);
            writer.write(data);
            writer.flush();
        }
    }

    public TradeLog[] getTradeLogs(UUID player) {
        return logs.stream().filter(log -> {
            Couple<OfflinePlayer> players = log.getPlayers();
            return players.getFirst().getUniqueId().equals(player) || players.getSecond().getUniqueId().equals(player);
        }).sorted(Comparator.comparing(TradeLog::getDate).reversed()).toArray(TradeLog[]::new);
    }

    @SneakyThrows
    private void loadLogs() {
        File file = plugin.getLoggerFile();
        try(JsonReader reader = new JsonReader(new FileReader(file))) {
            TradeLog[] loadedLogs = new Gson().fromJson(reader, TradeLog[].class);
            if(loadedLogs != null) logs.addAll(Arrays.asList(loadedLogs));
        }
    }

    public static void init(DTrade plugin) {
        logger = new TradeLogger(plugin);
    }
}

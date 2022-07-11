package org.dtrade.logging;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.dtrade.DTrade;
import org.dtrade.api.events.TradeLogAddedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TradeLogger {

    private final DTrade plugin;
    private final Set<TradeLog> logs;

    @SneakyThrows
    public TradeLogger(@NotNull DTrade plugin) {
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
            Set<String> serializedLogs = logs.stream().map(TradeLog::serialize).collect(Collectors.toSet());
            String data = new Gson().toJson(serializedLogs);
            writer.write(data);
            writer.flush();
        }
    }

    public TradeLog[] getTradeLogs(UUID player) {
        return logs.stream().filter(log -> log.getTraders().oneMeets(t -> t.getUniqueId().equals(player)))
                .sorted(Comparator.comparing(TradeLog::getDate).reversed()).toArray(TradeLog[]::new);
    }

    @SneakyThrows
    private void loadLogs() {
        File file = plugin.getLoggerFile();
        try(JsonReader reader = new JsonReader(new FileReader(file))) {
            Set<String> strs = new Gson().fromJson(reader, TypeToken.get(Set.class).getType());
            if(strs == null) return;
            Set<JsonElement> objs = strs.stream()
                    .map(JsonParser::parseString)
                    .collect(Collectors.toSet());
            for(JsonElement element : objs) logs.add(TradeLog.deserialize(element.getAsJsonObject()));
        }
    }

}

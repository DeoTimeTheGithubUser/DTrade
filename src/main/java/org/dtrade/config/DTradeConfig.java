package org.dtrade.config;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.plugin.Plugin;

@ConfigMeta(fileName = "config.yml") @Data
public class DTradeConfig implements Config {
    @Getter
    private static DTradeConfig config;

    @ConfigPath(path = "secondsUntilAccept")
    private int secondsUntilAccept = 5;
    @ConfigPath(path = "prefix")
    private String prefix = "&c[DTrade]";

    public static void init(Plugin plugin) {
        config = new DTradeConfig();
        config.load(plugin);
    }
}
package org.dtrade.config;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

public interface Config {

    @SneakyThrows
    default void load(Plugin plugin) {

        ConfigMeta meta = getConfigMeta(this);
        File file = new File(plugin.getDataFolder(), meta.fileName());
        Field[] configs = this.getClass().getDeclaredFields();

        boolean firstCreation = !file.exists();
        if(firstCreation) {
            plugin.getDataFolder().mkdirs();
            file.createNewFile();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for(Field field : configs) {
            field.setAccessible(true);
            ConfigPath path = getConfigPath(field);
            if(path == null) continue;
            if(firstCreation || config.get(path.path()) == null) config.set(path.path(), field.get(this));
            else field.set(this, config.get(path.path()));
        }
        config.save(file);
    }

    private static ConfigMeta getConfigMeta(Config config) {
        Class<?> clazz = config.getClass();
        return clazz.getAnnotation(ConfigMeta.class);
    }

    private static ConfigPath getConfigPath(Field field) {
        field.setAccessible(true);
        return field.getAnnotation(ConfigPath.class);
    }

}

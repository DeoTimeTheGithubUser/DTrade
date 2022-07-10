package org.dtrade.config;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;

public interface Config {

    @SneakyThrows
    static void load(Plugin plugin, Class<? extends Config> config) {

        ConfigMeta meta = getConfigMeta(config);
        File file = new File(plugin.getDataFolder(), meta.fileName());
        Field[] configs = config.getDeclaredFields();

        boolean firstCreation = !file.exists();
        if(firstCreation) {
            plugin.getDataFolder().mkdirs();
            file.createNewFile();
        }

        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        for(Field field : configs) {
            field.setAccessible(true);
            ConfigPath path = getConfigPath(field);
            if(path == null) continue;
            if(firstCreation || fileConfig.get(path.path()) == null) fileConfig.set(path.path(), field.get(null));
            else field.set(null, fileConfig.get(path.path()));
        }
        fileConfig.save(file);
    }

    private static ConfigMeta getConfigMeta(Class<? extends Config> clazz) {
        return clazz.getAnnotation(ConfigMeta.class);
    }

    private static ConfigPath getConfigPath(Field field) {
        field.setAccessible(true);
        return field.getAnnotation(ConfigPath.class);
    }

}

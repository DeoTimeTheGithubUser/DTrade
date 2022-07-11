package org.dtrade.config;

import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Config {

    @SneakyThrows
    static void load(final Plugin plugin, Class<? extends Config> config) {

        ConfigMeta meta = getConfigMeta(config);
        File file = new File(plugin.getDataFolder(), meta.fileName());
        Field[] configs = config.getDeclaredFields();

        YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        for(Field field : configs) {
            field.setAccessible(true);
            ConfigPath path = getConfigPath(field);
            if(path == null) continue;
            if(!fileConfig.contains(path.value())) {
                fileConfig.set(path.value(), field.get(null));
                ConfigComments comments = getConfigComments(field);
                if(comments != null) fileConfig.setComments(path.value(), Arrays.asList(comments.value()));
            }
            else {
                Object value = fileConfig.get(path.value());
                if(value instanceof List<?> list) value = list.toArray((Object[]) Array.newInstance(field.getType().getComponentType(), list.size()));
                field.set(null, value);
            }
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

    private static ConfigComments getConfigComments(Field field) {
        field.setAccessible(true);
        return field.getAnnotation(ConfigComments.class);
    }

}

package org.dtrade.util;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_18_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Utils {

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}

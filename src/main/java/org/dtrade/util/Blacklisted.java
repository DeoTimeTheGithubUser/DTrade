package org.dtrade.util;

import jdk.jshell.execution.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dtrade.DTrade;
import org.dtrade.config.DTradeConfig;

import java.util.Arrays;

public class Blacklisted {

    public static boolean isItemBlacklisted(ItemStack item) {
        if(item == null) return false;
        if(Arrays.stream(DTradeConfig.getBlacklistedMaterials()).map(Material::getMaterial).anyMatch(m -> item.getType() == m)) return true;
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;

        if(Utils.arrayContains(DTradeConfig.getBlacklistedNames(), meta.getDisplayName())) return true;

        if(meta.getLore() == null) return false;
        for(String line : meta.getLore()) if(Utils.arrayMatches(DTradeConfig.getBlacklistedLore(), line)) return true;
        return false;
    }

}

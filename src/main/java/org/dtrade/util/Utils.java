package org.dtrade.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class Utils {

    public static boolean hasPlayerJoinedBefore(String name) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(Objects::nonNull)
                .anyMatch(o -> o.getName().equals(name));
    }

    public static <T> boolean arrayContains(T[] arr, T item) {
        for(T i : arr) if(item.equals(i)) return true;
        return false;
    }

    public static boolean arrayMatches(String[] arr, Pattern pattern) {
        for(String str : arr) if(pattern.matcher(str).matches()) return true;
        return false;
    }

    public static boolean arrayMatches(String[] arr, String regex) {
        return arrayMatches(arr, Pattern.compile(regex));
    }

}

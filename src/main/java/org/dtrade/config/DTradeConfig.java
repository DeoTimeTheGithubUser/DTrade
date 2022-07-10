package org.dtrade.config;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.function.Supplier;

@ConfigMeta(fileName = "config.yml")
public class DTradeConfig implements Config {

    @ConfigPath(path = "trade.seconds_until_accept") @Getter
    private static int secondsUntilAccept = 5;
    @ConfigPath(path = "trade.seconds_until_accept") @Getter
    private static String tradeGuiTitle = "Trading with %player%";

    @ConfigPath(path = "trade.coin_input.prompt") @Getter
    private static String[] prompt = new String[]{"", "^^^^^^^^^^^^^^", "Enter coins to", "offer in trade."};


    @ConfigPath(path = "messages.prefix") @Getter
    private static String prefix = "&c[DTrade]";

    @ConfigPath(path = "messages.errors.not_enough_coins") @Getter
    private static String notEnoughCoins = "&cNot enough coins!";
    @ConfigPath(path = "messages.errors.invalid_coins") @Getter
    private static String invalidCoins = "&cInvalid amount of coins!";
    @ConfigPath(path = "messages.errors.cancelled_trade") @Getter
    private static String cancelledTrade = "&cYou cancelled the trade!";
    @ConfigPath(path = "messages.errors.other_cancelled_trade") @Getter
    private static String otherCancelledTrade = "&c%player% cancelled the trade!";
    @ConfigPath(path = "messages.errors.cant_trade_self") @Getter
    private static String cantTradeSelf = "&cYou cannot trade yourself!";

    private static <T> T make(Supplier<T> sup) {
        return sup.get();
    }

    public static String prefix(String str) {
        return color(prefix + "&r " + str);
    }

    public static String color(String str) {
        if(str == null) return null;
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String[] colorArr(String[] arr) {
        for (int i = 0; i < arr.length; i++) arr[i] = color(arr[i]);
        return arr;
    }
}
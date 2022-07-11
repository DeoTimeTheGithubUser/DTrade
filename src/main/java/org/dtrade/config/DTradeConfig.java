package org.dtrade.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

@ConfigMeta(fileName = "config.yml")
public class DTradeConfig implements Config {

    @ConfigPath("trade.shift_right_click_enabled") @Getter
    private static final boolean shiftRightClickEnabled = true;
    @ConfigPath("trade.seconds_until_accept") @Getter
    private static final int secondsUntilAccept = 5;
    @ConfigPath("trade.trade_gui_title") @Getter
    private static final String tradeGuiTitle = "Trading with %player%";
    @ConfigPath("trade.coin_input.prompt") @Getter
    private static final String[] prompt = new String[]{"", "^^^^^^^^^^^^^^", "Enter coins to", "offer in trade."};
    @ConfigPath("trade.trade_receipt.enabled") @Getter
    private static final boolean receiptEnabled = true;
    @ConfigPath("trade.trade_receipt.traded") @Getter
    private static final String receiptTraded = "&9[Traded Receipt]";
    @ConfigPath("trade.trade_receipt.received") @Getter
    private static final String receiptReceived = "&e[Received Receipt]";


    // messages
    @ConfigPath("messages.prefix") @Getter
    private static final String prefix = "&7[&bDTrade&7]";
    @ConfigPath("messages.now_trading_with") @Getter
    private static final String nowTradingWith = "&aYou are now trading with %player%.";
    @ConfigPath("messages.sent_request_to") @Getter
    private static final String sentRequestTo = "&aYou sent trade request to %player%.";
    @ConfigPath("messages.received_request_from") @Getter
    private static final String receivedRequestFrom = "&aYou received a trade request from %player%.";
    @ConfigPath("messages.trade_complete") @Getter
    private static final String tradeComplete = "&aCompleted trade with %player%!";
    @ConfigPath("messages.item_dropped_because_no_space") @Getter
    private static final String itemDroppedBecauseNoSpace = "&cAn item was dropped on the ground because you did not have enough space in your inventory!";

    // errors
    @ConfigPath("messages.errors.no_permission") @Getter
    private static final String noPermission = "&cYou do not have permission to perform this action!";
    @ConfigPath("messages.errors.no_username_entered") @Getter
    private static final String noUsernameEntered = "&cYou need to enter a player's username!";
    @ConfigPath("messages.errors.not_enough_coins") @Getter
    private static final String notEnoughCoins = "&cNot enough coins!";
    @ConfigPath("messages.errors.invalid_coins") @Getter
    private static final String invalidCoins = "&cInvalid amount of coins!";
    @ConfigPath("messages.errors.cancelled_trade") @Getter
    private static final String cancelledTrade = "&cYou cancelled the trade!";
    @ConfigPath("messages.errors.other_cancelled_trade") @Getter
    private static final String otherCancelledTrade = "&c%player% cancelled the trade!";
    @ConfigPath("messages.errors.cant_trade_self") @Getter
    private static final String cantTradeSelf = "&cYou cannot trade yourself!";
    @ConfigPath("messages.errors.player_requested_not_exist") @Getter
    private static final String playerRequestedNotExist = "&cThat player is not online!";
    @ConfigPath("messages.errors.already_outgoing_request") @Getter
    private static final String alreadyOutgoingRequest = "&cYou already have an outgoing request!";

    private static <T> T make(Supplier<T> sup) {
        return sup.get();
    }

    public static String prefix(String str) {
        return color(prefix + "&r " + str);
    }

    public static String prefix(String str, Player player) {
        return prefix(str).replaceAll("%player%", player.getName());
    }

    public static String color(String str) {
        if(str == null) return null;
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String color(String str, Player player) {
        if(str == null) return null;
        return ChatColor.translateAlternateColorCodes('&', str).replaceAll("%player%", player.getName());
    }

    public static String[] colorArr(String[] arr) {
        for (int i = 0; i < arr.length; i++) arr[i] = color(arr[i]);
        return arr;
    }
}
package org.dtrade.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

@ConfigMeta(fileName = "config.yml")
public class DTradeConfig implements Config {

    // trade

    @ConfigPath("trade.seconds_until_accept") @Getter
    private static int secondsUntilAccept = 5;

    @ConfigPath("trade.trade_gui_title") @Getter
    private static String tradeGuiTitle = "Trading with %player%";

    @ConfigPath("trade.request.shift_right_click_enabled") @Getter
    private static boolean shiftRightClickEnabled = true;
    @ConfigPath("trade.request.seconds_until_request_timeout") @Getter
    private static int secondsUntilRequestTimeout = 10;
    @ConfigPath("trade.request.request_to_expired") @Getter
    private static String requestToExpired = "&cThe trade request to %player% has expired.";
    @ConfigPath("trade.request.request_from_expired") @Getter
    private static String requestFromExpired = "&cThe trade request from %player% has expired.";


    @ConfigPath("trade.coin_input.minimum_coin_input") @Getter
    private static long minCoinInput = 0;
    @ConfigPath("trade.coin_input.maximum_coin_input") @Getter
    private static long maxCoinInput = Long.MAX_VALUE;
    @ConfigPath("trade.coin_input.prompt") @Getter
    private static String[] prompt = new String[]{"", "^^^^^^^^^^^^^^", "Enter coins to", "offer in trade."};

    @ConfigPath("trade.trade_receipt.enabled") @Getter
    private static boolean receiptEnabled = true;
    @ConfigPath("trade.trade_receipt.traded") @Getter
    private static String receiptTraded = "&9[Traded Receipt]";
    @ConfigPath("trade.trade_receipt.received") @Getter
    private static String receiptReceived = "&e[Received Receipt]";

    @ConfigPath("trade.blacklisted.blacklisted_message") @Getter
    private static String blacklistedMessage = "&cThat item is blacklisted from trading!";
    @ConfigPath("trade.blacklisted.blacklisted_materials") @Getter @ConfigComments("Any item with a blacklisted material will not be able to be traded.")
    private static String[] blacklistedMaterials = new String[]{};
    @ConfigPath("trade.blacklisted.blacklisted_names") @Getter @ConfigComments("Any item with a name that matches one of the regex will not be able to be traded.")
    private static String[] blacklistedNames = new String[]{};
    @ConfigPath("trade.blacklisted.blacklisted_lore") @Getter @ConfigComments("Any item with a line of lore that matches one of the regex will not be able to be traded.")
    private static String[] blacklistedLore = new String[]{};


    // messages
    @ConfigPath("messages.prefix") @Getter
    private static String prefix = "&7[&bDTrade&7]";
    @ConfigPath("messages.now_trading_with") @Getter
    private static String nowTradingWith = "&aYou are now trading with %player%.";
    @ConfigPath("messages.sent_request_to") @Getter
    private static String sentRequestTo = "&aYou sent trade request to %player%.";
    @ConfigPath("messages.received_request_from") @Getter
    private static String receivedRequestFrom = "&aYou received a trade request from %player%.";
    @ConfigPath("messages.trade_complete") @Getter
    private static String tradeComplete = "&aCompleted trade with %player%!";
    @ConfigPath("messages.item_dropped_because_no_space") @Getter
    private static String itemDroppedBecauseNoSpace = "&cAn item was dropped on the ground because you did not have enough space in your inventory!";

    // errors
    @ConfigPath("messages.errors.no_permission") @Getter
    private static String noPermission = "&cYou do not have permission to perform this action!";
    @ConfigPath("messages.errors.no_username_entered") @Getter
    private static String noUsernameEntered = "&cYou need to enter a player's username!";
    @ConfigPath("messages.errors.not_enough_coins") @Getter
    private static String notEnoughCoins = "&cNot enough coins!";
    @ConfigPath("messages.errors.invalid_coins") @Getter
    private static String invalidCoins = "&cInvalid amount of coins!";
    @ConfigPath("messages.errors.cancelled_trade") @Getter
    private static String cancelledTrade = "&cYou cancelled the trade!";
    @ConfigPath("messages.errors.other_cancelled_trade") @Getter
    private static String otherCancelledTrade = "&c%player% cancelled the trade!";
    @ConfigPath("messages.errors.cant_trade_self") @Getter
    private static String cantTradeSelf = "&cYou cannot trade yourself!";
    @ConfigPath("messages.errors.player_requested_not_exist") @Getter
    private static String playerRequestedNotExist = "&cThat player is not online!";
    @ConfigPath("messages.errors.already_outgoing_request") @Getter
    private static String alreadyOutgoingRequest = "&cYou already have an outgoing request!";

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
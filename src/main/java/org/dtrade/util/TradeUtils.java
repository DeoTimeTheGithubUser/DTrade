package org.dtrade.util;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class TradeUtils {

    public static int convertSlotToTradeIndex(int slot) {
        return ((slot / 9) * 4) + (slot % 9);
    }

    public static int convertOtherSlotToTradeIndex(int slot) {
        return ((slot / 9) * 4) + (slot % 9) - 5;
    }

    public static boolean isMiddle(int slot) {
        return slot % 9 == 4;
    }

    public static boolean isOtherTraderSlot(int slot) {
        return slot % 9 > 4;
    }

    public static String getTraderTradedReceipt(Trader trader) {

        List<String> lines = new ArrayList<String>();

        lines.add("\u00a7aCoins: \u00a7e" + trader.getOfferedCoins());

        lines.add("");
        lines.add("\u00a7aItems:");

        for (ItemStack item : trader.getOfferedItems()) {
            lines.add("\u00a77• " + ItemUtils.getRealName(item) + " \u00a78x" + item.getAmount());
        }

        if (trader.getOfferedItems().size() == 0) lines.add("\u00a78none");

        String combined = "";
        for (String s : lines) {
            combined += s + "\n";
        }

        return combined.substring(0, combined.length() - 1);

    }

    public static String getTraderReceivedReceipt(Trader trader) {

        List<String> lines = new ArrayList<String>();

        lines.add("\u00a7aCoins: \u00a7e" + Trade.getTradeOf(trader).getCouple().other(trader).getOfferedCoins());

        lines.add("");
        lines.add("\u00a7aItems:");

        for (ItemStack item : Trade.getTradeOf(trader).getCouple().other(trader).getOfferedItems()) {
            lines.add("\u00a77• " + ItemUtils.getRealName(item) + " \u00a78x" + item.getAmount());
        }

        if (Trade.getTradeOf(trader).getCouple().other(trader).getOfferedItems().size() == 0) lines.add("\u00a78none");

        String combined = "";
        for (String s : lines) {
            combined += s + "\n";
        }

        return combined.substring(0, combined.length() - 1);

    }

}

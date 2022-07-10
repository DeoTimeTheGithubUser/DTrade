package org.dtrade.util;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.dtrade.trade.Trader;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
@ExtensionMethod({ItemUtils.class})
public final class TradeUtils {

    public static int convertSlotToTradeIndex(int slot) {
        return ((slot / 9) * 4) + (slot % 9);
    }

    public static int convertOtherSlotToTradeIndex(int slot) {
        return (((slot / 9) * 4) + (slot % 9)) - 5;
    }

    public static int convertTradeIndexToSlot(int index) {
        return ((index / 4) * 9) + (index % 4);
    }

    public static int convertOtherTradeIndexToSlot(int index) {
        return (((index / 4) * 9) + (index % 4)) + 5;
    }

    public static boolean isMiddle(int slot) {
        return slot % 9 == 4;
    }

    public static boolean isOtherTraderSlot(int slot) {
        return slot % 9 > 4;
    }

    public static String getTradeReceipt(Trader trader, boolean received) {
        Trader target = received ? trader.getPartner() : trader;
        List<String> lines = new ArrayList<>();
        lines.add("\u00a7aCoins: \u00a7e" + target.getOfferedCoins());
        lines.add("");
        lines.add("\u00a7aItems:");
        for (ItemStack item : target.getOfferedItems()) lines.add("\u00a77• " + ItemUtils.getRealName(item) + " \u00a78x" + item.getAmount());
        if (target.getOfferedItems().size() == 0) lines.add("\u00a78none");
        String combined = "";
        for (String s : lines) combined += s + "\n";
        return combined.substring(0, combined.length() - 1);
    }

}

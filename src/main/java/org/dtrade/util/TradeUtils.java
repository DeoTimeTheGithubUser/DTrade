package org.dtrade.util;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.inventory.ItemStack;
import org.dtrade.config.DTradeConfig;
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

    public static BaseComponent[] getTradeReceipt(Trader trader) {
        ComponentBuilder builder = new ComponentBuilder();
        TextComponent message = new TextComponent(DTradeConfig.prefix(DTradeConfig.getTradeComplete(), trader.getPlayer()));
        TextComponent tradedReceipt = new TextComponent(DTradeConfig.color(DTradeConfig.getReceiptTraded()));
        TextComponent receivedReceipt = new TextComponent(DTradeConfig.color(DTradeConfig.getReceiptReceived()));
        tradedReceipt.setHoverEvent(getTradeReceiptTooltip(trader, false));
        receivedReceipt.setHoverEvent(getTradeReceiptTooltip(trader, true));
        builder
                .append(message)
                .append("\n")
                .append(tradedReceipt)
                .append(receivedReceipt);
        return builder.create();

    }

    public static HoverEvent getTradeReceiptTooltip(Trader trader, boolean received) {
        Trader target = received ? trader.getPartner() : trader;
        List<String> lines = new ArrayList<>();
        lines.add("\u00a7aCoins: \u00a7e" + target.getOfferedCoins());
        lines.add("");
        lines.add("\u00a7aItems:");
        for (ItemStack item : target.getOfferedItems()) lines.add("\u00a77• " + ItemUtils.getRealName(item) + " \u00a78x" + item.getAmount());
        if (target.getOfferedItems().size() == 0) lines.add("\u00a78none");
        String combined = "";
        for (String s : lines) combined += s + "\n";
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(combined.substring(0, combined.length() - 1)));
    }

}

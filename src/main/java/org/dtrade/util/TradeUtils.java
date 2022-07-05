package org.dtrade.util;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.inventory.ItemStack;
import org.dtrade.trade.Trade;
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
        return ((slot / 9) * 4) + (slot % 9) - 5;
    }

    public static boolean isMiddle(int slot) {
        return slot % 9 == 4;
    }

    public static boolean isOtherTraderSlot(int slot) {
        return slot % 9 > 4;
    }


    public static BaseComponent[] getTradeReceipt(Trader trader, boolean received) {
        Trader target = received ? trader.getTrade().getCouple().other(trader) : trader;

        ComponentBuilder builder = new ComponentBuilder();
        builder.append("\u00a7aCoins: \u00a7e" + target.getOfferedCoins());
        builder.append("");
        builder.append("\u00a7aItems:");
        target.getOfferedItems().forEach(item -> {
            TextComponent comp = new TextComponent("\u00a77• " + ItemUtils.getRealName(item) + " \u00a78x" + item.getAmount());
            comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(null, 1, ItemTag.ofNbt(item.getNbt()))));
            builder.append(comp);
        });
        return builder.create();
    }

}

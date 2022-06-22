package org.dtrade.util;

import lombok.experimental.UtilityClass;

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

}

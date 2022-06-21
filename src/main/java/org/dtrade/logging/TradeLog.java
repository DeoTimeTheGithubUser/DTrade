package org.dtrade.logging;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.dtrade.trade.Trade;

import java.util.Date;
import java.util.UUID;

@Data @AllArgsConstructor
public class TradeLog {

    private Date date;

    private UUID trader1;
    private UUID trader2;

    private ItemStack[] trader1Items;
    private ItemStack[] trader2Items;

    public static TradeLog createLog(Trade trade) {
        // TODO
        return null;
    }
}

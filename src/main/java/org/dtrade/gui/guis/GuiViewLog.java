package org.dtrade.gui.guis;

import lombok.experimental.ExtensionMethod;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.dtrade.gui.management.Gui;
import org.dtrade.logging.TradeLog;
import org.dtrade.packets.TradeView;
import org.dtrade.util.Couple;
import org.dtrade.util.ItemUtils;
import org.dtrade.util.TradeUtils;

import java.util.concurrent.atomic.AtomicBoolean;

@ExtensionMethod({ItemUtils.class})
public class GuiViewLog extends Gui {

    private final TradeLog log;

    public GuiViewLog(TradeLog log) {
        super(formatNames(log), 54);
        this.log = log;
        initGuis();
    }

    private void initGuis() {
        for(int i = 0; i < getSize(); i++) if(i % 9 == 4) setItem(i, ItemUtils.createMenuGlass());
        setItem(49, createMoneyItem());
        log.getPlayers().both(p -> {
            boolean first = log.getPlayers().getFirst().getUniqueId().equals(p.getUniqueId());
            ItemStack[] items = first ? log.getItems().getFirst() : log.getItems().getSecond();
            for(int i = 0; i < items.length; i++) {
                int slot = first ? TradeUtils.convertTradeIndexToSlot(i) : TradeUtils.convertOtherTradeIndexToSlot(i);
                setItem(slot, items[i]);
            }
        });
    }

    private ItemStack createMoneyItem() {
        Couple<OfflinePlayer> players = log.getPlayers();
        Couple<Long> offered = log.getOfferedCoins();
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        item
                .setDisplayName("\u00a7aMoney Offered")
                .addLore("")
                .addLore(String.format("\u00a7e%s traded \u00a7c%s \u00a7ecoins.", players.getFirst().getName(), offered.getFirst()))
                .addLore("")
                .addLore(String.format("\u00a7e%s traded \u00a7d%s \u00a7ecoins.", players.getSecond().getName(), offered.getSecond()));
        return item;

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private static String formatNames(TradeLog log) {
        Couple<OfflinePlayer> players = log.getPlayers();
        return players.getFirst().getName() + "              " + players.getSecond().getName();
    }

}

package org.dtrade.gui.guis;

import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.dtrade.DTrade;
import org.dtrade.gui.management.Gui;
import org.dtrade.logging.TradeLog;
import org.dtrade.logging.TradeLogger;
import org.dtrade.util.Couple;
import org.dtrade.util.ItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiTradeLogs extends Gui {

    private final DTrade plugin;
    private final UUID target;
    private final int page;
    private final Map<Integer, TradeLog> logMap = new HashMap<>();

    public GuiTradeLogs(DTrade plugin, UUID playerUUID, int page) {
        super("Trade logs of " + Bukkit.getOfflinePlayer(playerUUID).getName(), 54);
        this.plugin = plugin;
        this.target = playerUUID;
        this.page = page;
        initGuis();
    }

    public GuiTradeLogs(DTrade plugin, OfflinePlayer player) {
        this(plugin, player.getUniqueId(), 0);
    }

    private void initGuis() {

        TradeLog[] logs = plugin.getTradeLogger().getTradeLogs(target);

        for(int i = 45; i <= 53; i++) setItem(i, ItemUtils.createMenuGlass());
        int offset = page * 45;
        if(page != 0) setItem(48, ItemUtils.createGui(Material.ARROW, "\u00a7ePrevious Page"));
        if((offset + 45) < logs.length) setItem(50, ItemUtils.createGui(Material.ARROW, "\u00a7eNext Page"));
        setItem(49, ItemUtils.createGui(Material.BARRIER, "\u00a7cClose"));

        for(int i = offset; i < (45 + offset); i++) {
            if(i >= logs.length) break;
            TradeLog log = logs[i];
            logMap.put(firstEmpty(), log);
            setItem((i - offset), new TradeLogItem(log));
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot == 48 && page != 0) {
            event.getWhoClicked().openInventory(new GuiTradeLogs(plugin, target, page - 1));
            return;
        } else if (slot == 50 && firstEmpty() == -1) {
            event.getWhoClicked().openInventory(new GuiTradeLogs(plugin, target, page + 1));
            return;
        } else if (slot == 49) {
            event.getWhoClicked().closeInventory();
            return;
        }

        if(!logMap.containsKey(slot)) return;
        else event.getWhoClicked().openInventory(new GuiViewLog(logMap.get(slot)));
    }

    @ExtensionMethod({ItemUtils.class})
    class TradeLogItem extends ItemStack {
        TradeLogItem(TradeLog log) {
            super(Material.PLAYER_HEAD);
            Couple<OfflinePlayer> players = log.getTraders();
            OfflinePlayer tradedWith = players.getFirst().getUniqueId().equals(target)
                    ? players.getSecond() : players.getFirst();
            SkullMeta meta = (SkullMeta) getItemMeta();
            meta.setOwningPlayer(tradedWith);
            setItemMeta(meta);

            ((ItemStack) this).setDisplayName("\u00a7c" + tradedWith.getName());
            ((ItemStack) this).addLore("\u00a77At: " + log.getDate(), "", "\u00a7eClick to view");
        }
    }

}

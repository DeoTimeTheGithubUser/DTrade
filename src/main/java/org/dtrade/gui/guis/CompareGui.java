package org.dtrade.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.dtrade.DTrade;
import org.dtrade.gui.management.Gui;
import org.dtrade.gui.management.GuiUtils;
import org.dtrade.trade.Trade;

public class CompareGui extends Gui {

    private static String NAME = "Test Compare";
    private static final int SIZE = 54;

    public CompareGui() {

        super(NAME, SIZE);

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(DTrade.getInstance(), () -> {

        }, 1L);
    }

}

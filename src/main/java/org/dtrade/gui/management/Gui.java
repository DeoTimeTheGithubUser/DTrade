package org.dtrade.gui.management;

import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public abstract class Gui extends CraftInventoryCustom {

    public Gui(String name, int size) {
        super(null, validateSize(size), name);
        GuiManager.getGuiManager().addGui(this);
    }

    public void onOpen(InventoryOpenEvent event){}
    public void onClose(InventoryCloseEvent event){}
    public void onClick(InventoryClickEvent event){}
    public void onDrag(InventoryDragEvent event){}

    public void dispose() {
        this.getViewers().forEach(p -> {
            if(p.getOpenInventory().getTopInventory().equals(this)) p.closeInventory();
        });
    }

    private static int validateSize(int size) {
        if(size % 9 != 0) throw new IllegalArgumentException("Inventory size must be a multiple of 9.");
        return size;
    }
}

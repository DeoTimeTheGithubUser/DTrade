package org.dtrade.gui.management;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.dtrade.DTrade;

import java.util.HashSet;
import java.util.Set;

public class GuiManager implements Listener {

    private static GuiManager instance;

    public GuiManager() {
        instance = this;
    }

    public static GuiManager getGuiManager() {
        return instance;
    }

    private final Set<Gui> GUIS = new HashSet<>();

    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event) {
        final Gui Gui = findGui(event.getInventory());
        if(Gui != null) Gui.onOpen(event);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        final Gui Gui = findGui(event.getInventory());
        if(Gui != null) Gui.onClose(event);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        final Gui Gui = findGui(event.getInventory());
        if(Gui != null) Gui.onClick(event);

    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        final Gui Gui = findGui(event.getInventory());
        if(Gui != null) Gui.onDrag(event);
    }

    public void addGui(Gui Gui) {
        this.GUIS.add(Gui);
    }

    // Finds a Gui given a normal Inventory
    public Gui findGui(Inventory inventory) {
        if(!(inventory instanceof Gui)) return null;
        return this.GUIS
                .stream()
                .filter(gui -> gui.equals(inventory))
                .findFirst()
                .orElse(null);
    }
}

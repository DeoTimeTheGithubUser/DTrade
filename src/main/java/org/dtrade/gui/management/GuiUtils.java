package org.dtrade.gui.management;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiUtils {

    public static Inventory getResultingClick(Inventory inventory, InventoryInteractEvent event) {

        Inventory newInventory = Bukkit.createInventory(null, inventory.getSize(), event.getView().getTitle());

        for (int i = 0; i < inventory.getSize(); i++) {
            newInventory.setItem(i, inventory.getItem(i).clone());
        }

        if (event instanceof InventoryClickEvent) {

            InventoryClickEvent clickEvent = (InventoryClickEvent) event;

            switch (clickEvent.getClick()) {

                case LEFT: {

                    if (clickEvent.getWhoClicked().getItemOnCursor().getType() != Material.AIR) {

                    }

                }

            }

        } else if (event instanceof InventoryDragEvent) {

            InventoryDragEvent dragEvent = (InventoryDragEvent) event;

        }

        return newInventory;
    }

    public static boolean ItemStackEquals(ItemStack item1, ItemStack item2, boolean ignoreStackSize) {

        if (item1 == null || item2 == null) return false;

        net.minecraft.world.item.ItemStack nms1 = CraftItemStack.asNMSCopy(item1);
        net.minecraft.world.item.ItemStack nms2 = CraftItemStack.asNMSCopy(item2);

        // u() is the item's nbt
        if (nms1.u().toString().equals(nms2.u().toString()) && item1.getType().equals(item2.getType())) {
            if (ignoreStackSize) return true;
            else return item1.getAmount() == item2.getAmount();
        }

        return false;
    }

}

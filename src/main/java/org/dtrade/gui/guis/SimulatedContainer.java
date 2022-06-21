package org.dtrade.gui.guis;

import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.inventory.InventoryClickType;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventoryCustom;
import org.bukkit.inventory.InventoryView;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimulatedContainer extends Container {

    public SimulatedContainer(@Nullable Containers<?> containers, int i) {
        super(containers, i);
    }

    @Override
    public InventoryView getBukkitView() {
        return null;
    }

    private void b(int i, int j, InventoryClickType inventoryclicktype, EntityHuman entityhuman) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method b = Container.class.getDeclaredMethod("b", int.class, int.class, InventoryClickType.class, EntityHuman.class);
        b.setAccessible(true);
        b.invoke(this, i, j, inventoryclicktype, entityhuman);
        System.out.println("method overrided");
    }

    @Override
    public boolean a(EntityHuman entityHuman) {
        return false;
    }

}

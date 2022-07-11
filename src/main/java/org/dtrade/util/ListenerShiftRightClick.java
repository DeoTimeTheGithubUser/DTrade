package org.dtrade.util;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.dtrade.DTrade;
import org.dtrade.config.DTradeConfig;
import org.dtrade.trade.TradeRequest;

@RequiredArgsConstructor
public class ListenerShiftRightClick implements Listener {
    private final DTrade plugin;
    @EventHandler
    private void onInteract(PlayerInteractEntityEvent event) {
        if(!DTradeConfig.isShiftRightClickEnabled() || !event.getPlayer().isSneaking() || event.getHand() == EquipmentSlot.HAND) return;
        if(!(event.getRightClicked() instanceof Player requested)) return;
        TradeRequest.createTradeRequest(plugin, event.getPlayer(), requested);
    }
}

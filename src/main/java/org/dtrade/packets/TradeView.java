package org.dtrade.packets;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.TradeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class TradeView implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        ((CraftPlayer) e.getPlayer()).getHandle().b.a.m.pipeline().addBefore("packet_handler", "dtrade-tradeview", new ChannelDuplexHandler() {

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception{

                Packet<?> packet = (Packet<?>) o;

                try {

                    if (packet instanceof PacketPlayOutSetSlot) {

                        Trader trader = Trader.getTrader(e.getPlayer());

                        Field slotField = PacketPlayOutSetSlot.class.getDeclaredField("e");
                        slotField.setAccessible(true);

                        int slot = (int) slotField.get(packet);

                        if (trader == null || TradeUtils.isMiddle(slot)) {
                            super.write(channelHandlerContext, o, channelPromise);
                            return;
                        }

                        Trade trade = Trade.getTradeOf(trader);

                        ItemStack slotDisplayItem = new ItemStack(Material.AIR);

                        if (!TradeUtils.isOtherTraderSlot(slot) && trader.getOfferedItems().size() > TradeUtils.convertSlotToTradeIndex(slot))
                            slotDisplayItem = trader.getOfferedItems().get(TradeUtils.convertSlotToTradeIndex(slot));
                        else if (trade.getCouple().other(trader).getOfferedItems().size() > TradeUtils.convertOtherSlotToTradeIndex(slot))
                            slotDisplayItem = trade.getCouple().other(trader).getOfferedItems().get(TradeUtils.convertOtherSlotToTradeIndex(slot));

                        net.minecraft.world.item.ItemStack display = CraftItemStack.asNMSCopy(slotDisplayItem);

                        Field item = PacketPlayOutSetSlot.class.getDeclaredField("f");
                        item.setAccessible(true);
                        item.set(packet, display);

                    } else if (packet instanceof PacketPlayOutWindowItems) {

                        Trader trader = Trader.getTrader(e.getPlayer());

                        Field slotsField = PacketPlayOutWindowItems.class.getDeclaredField("c");
                        slotsField.setAccessible(true);


                        if (trader == null) {
                            super.write(channelHandlerContext, o, channelPromise);
                            return;
                        }

                        Trade trade = Trade.getTradeOf(trader);

                        List<net.minecraft.world.item.ItemStack> items = (List<net.minecraft.world.item.ItemStack>) slotsField.get(packet);

                        for (int i = 0; i < items.size(); i++) {

                            ItemStack slotDisplayItem = CraftItemStack.asBukkitCopy(items.get(i));

                            if (!TradeUtils.isOtherTraderSlot(i) && !TradeUtils.isOtherTraderSlot(i)) {
                                if (trader.getOfferedItems().size() > TradeUtils.convertSlotToTradeIndex(i))
                                    slotDisplayItem = trader.getOfferedItems().get(TradeUtils.convertSlotToTradeIndex(i));
                            } else if (!TradeUtils.isMiddle(i)) {
                                if (trade.getCouple().other(trader).getOfferedItems().size() > TradeUtils.convertOtherSlotToTradeIndex(i))
                                    slotDisplayItem = trade.getCouple().other(trader).getOfferedItems().get(TradeUtils.convertOtherSlotToTradeIndex(i));
                            }

                            net.minecraft.world.item.ItemStack display = CraftItemStack.asNMSCopy(slotDisplayItem);

                            items.set(i, display);

                        }

                        slotsField.set(packet, items);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.write(channelHandlerContext, o, channelPromise);
            }

        });


    }

}

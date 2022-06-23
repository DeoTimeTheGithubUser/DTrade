package org.dtrade.packets;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.experimental.ExtensionMethod;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.dtrade.gui.guis.TradeGui;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.ItemUtils;
import org.dtrade.util.ReflectUtils;
import org.dtrade.util.TradeUtils;

import java.lang.reflect.Field;
import java.util.List;

@ExtensionMethod({ItemUtils.class})
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
                        Trade trade = trader.getTrade();
                        if(trade == null) {
                            super.write(channelHandlerContext, o, channelPromise);
                            return;
                        }

                        Field slotField = PacketPlayOutSetSlot.class.getDeclaredField("e");
                        slotField.setAccessible(true);

                        int slot = (int) slotField.get(packet);

                        if(slot == 49) ReflectUtils.setField(packet, "f", CraftItemStack.asNMSCopy(createAcceptButton(trade, trader)));
                        if (slot % 9 == 4 && slot != 49) ReflectUtils.setField(packet, "f", CraftItemStack.asNMSCopy(createMenuGlass()));
                        if (trader == null || TradeUtils.isMiddle(slot)) {
                            super.write(channelHandlerContext, o, channelPromise);
                            return;
                        }
                        ItemStack slotDisplayItem = new ItemStack(Material.AIR);

                        if (!TradeUtils.isOtherTraderSlot(slot) && trader.getOfferedItems().size() > TradeUtils.convertSlotToTradeIndex(slot) && slot != -1)
                            slotDisplayItem = trader.getOfferedItems().get(TradeUtils.convertSlotToTradeIndex(slot));
                        else if (trade.getCouple().other(trader).getOfferedItems().size() > TradeUtils.convertOtherSlotToTradeIndex(slot) && slot != -1)
                            slotDisplayItem = trade.getCouple().other(trader).getOfferedItems().get(TradeUtils.convertOtherSlotToTradeIndex(slot));

                        net.minecraft.world.item.ItemStack display = CraftItemStack.asNMSCopy(slotDisplayItem);

                        ReflectUtils.setField(packet, "f", display);
                    } else if (packet instanceof PacketPlayOutWindowItems) {

                        Trader trader = Trader.getTrader(e.getPlayer());
                        Trade trade = trader.getTrade();

                        if (trader == null) {
                            super.write(channelHandlerContext, o, channelPromise);
                            return;
                        }

                        Field slotsField = PacketPlayOutWindowItems.class.getDeclaredField("c");
                        slotsField.setAccessible(true);


                        List<net.minecraft.world.item.ItemStack> items = (List<net.minecraft.world.item.ItemStack>) slotsField.get(packet);

                        for (int i = 0; i < items.size(); i++) {

                            ItemStack slotDisplayItem = CraftItemStack.asBukkitCopy(items.get(i));

                            if (!TradeUtils.isOtherTraderSlot(i) && !TradeUtils.isOtherTraderSlot(i) && !TradeUtils.isMiddle(i)) {
                                if (trader.getOfferedItems().size() > TradeUtils.convertSlotToTradeIndex(i))
                                    slotDisplayItem = trader.getOfferedItems().get(TradeUtils.convertSlotToTradeIndex(i));
                            } else if (!TradeUtils.isMiddle(i)) {
                                if (trade.getCouple().other(trader).getOfferedItems().size() > TradeUtils.convertOtherSlotToTradeIndex(i))
                                    slotDisplayItem = trade.getCouple().other(trader).getOfferedItems().get(TradeUtils.convertOtherSlotToTradeIndex(i));
                            }

                            net.minecraft.world.item.ItemStack display = CraftItemStack.asNMSCopy(slotDisplayItem);

                            items.set(i, display);
                        }
                        items.set(49, CraftItemStack.asNMSCopy(createAcceptButton(trade, trader)));
                        for (int i = 0; i < TradeGui.SIZE; i++) if (i % 9 == 4 && i != 49) items.set(i, CraftItemStack.asNMSCopy(createMenuGlass()));
                        slotsField.set(packet, items);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.write(channelHandlerContext, o, channelPromise);
            }

        });


    }

    private static ItemStack createMenuGlass() {
        return new ItemStack(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ");
    }

    private static ItemStack createAcceptButton(Trade trade, Trader trader) {
        Trader otherTrader = trade.getCouple().other(trader);
        ItemStack acceptTradeButton = new ItemStack(Material.AIR);
        acceptTradeButton.setType(trade.getCouple().oneMeets(Trader::isAcceptedTrade) ? Material.GREEN_WOOL : Material.RED_WOOL);

        acceptTradeButton.setDisplayName("\u00a7aAccept trade");
        acceptTradeButton.clearLore();
        String lore = "";
        if(trader.isAcceptedTrade() && otherTrader.isAcceptedTrade()) lore = "\u00a77You have both accepted the trade.";
        else if(trader.isAcceptedTrade()) lore = "\u00a77You accepted the trade.";
        else if(otherTrader.isAcceptedTrade()) lore = "\u00a77" + otherTrader.getPlayer().getName() + " has accepted the trade.";
        else lore = "\u00a77No one accepted the trade.";
        acceptTradeButton.addLore(lore);
        acceptTradeButton.setAmount(trade.getSecondsUntilAccept() == -1 ? 1 : trade.getSecondsUntilAccept());

        if(trader.isAcceptedTrade() && otherTrader.isAcceptedTrade()) acceptTradeButton.addGlint();

        return acceptTradeButton;
    }

}

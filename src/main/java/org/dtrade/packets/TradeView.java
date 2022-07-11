package org.dtrade.packets;

import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dtrade.DTrade;
import org.dtrade.gui.guis.GuiTrade;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.ItemUtils;
import org.dtrade.util.ReflectUtils;
import org.dtrade.util.TradeUtils;

import java.util.List;

@ExtensionMethod({ItemUtils.class})
public class TradeView {

    private TradeView(DTrade plugin) {
        PacketHandler.getPacketHandler().subscribe(PacketPlayOutSetSlot.class, getSetSlotWriter(plugin));
        PacketHandler.getPacketHandler().subscribe(PacketPlayOutWindowItems.class, getWindowItemsWriter(plugin));
    }

    private PacketHandler.PacketWriteSubscriber<PacketPlayOutSetSlot> getSetSlotWriter(DTrade plugin) {
        return new PacketHandler.PacketWriteSubscriber<>() {
            @Override @SneakyThrows
            public void onWrite(Player player, PacketPlayOutSetSlot packet) {
                Trader trader = Trader.getTrader(player);
                if(trader == null) return;
                Trader partner = trader.getPartner();
                Trade trade = trader.getTrade();
                if(trade == null || trade.isCancelled()) return;
                boolean econEnabled = plugin.getEconomyHandler().supportsEconomy();
                int slot = (int) ReflectUtils.getField(packet, "e");
                int c = (int) ReflectUtils.getField(packet, "c");
                if (c == 0 || slot > 53) return;
                if (TradeUtils.isMiddle(slot)) {
                    if(slot == 40 && econEnabled) ReflectUtils.setField(packet, "f", CraftItemStack.asNMSCopy(createMoneyButton(trader)));
                    else if(slot == 49) ReflectUtils.setField(packet, "f", CraftItemStack.asNMSCopy(createAcceptButton(trade, trader)));
                    else if (slot % 9 == 4) ReflectUtils.setField(packet, "f", CraftItemStack.asNMSCopy(ItemUtils.createMenuGlass()));
                    return;
                }
                ItemStack slotDisplayItem = new ItemStack(Material.AIR);
                int convertedSlot = TradeUtils.convertOtherSlotToTradeIndex(slot);
                if(convertedSlot < 0) {
                    return;
                }
                if (!TradeUtils.isOtherTraderSlot(slot) && trader.getOfferedItems().size() > convertedSlot)
                    slotDisplayItem = trader.getOfferedItems().get(convertedSlot);
                else if (partner.getOfferedItems().size() > convertedSlot)
                    slotDisplayItem = partner.getOfferedItems().get(convertedSlot);
                net.minecraft.world.item.ItemStack display = CraftItemStack.asNMSCopy(slotDisplayItem);
                ReflectUtils.setField(packet, "f", display);
            }
        };
    }

    private PacketHandler.PacketWriteSubscriber<PacketPlayOutWindowItems> getWindowItemsWriter(DTrade plugin) {
        return new PacketHandler.PacketWriteSubscriber<>() {
            @Override
            @SneakyThrows
            @SuppressWarnings("unchecked")
            public void onWrite(Player player, PacketPlayOutWindowItems packet) {
                Trader trader = Trader.getTrader(player);
                if (trader == null) return;
                Trader partner = trader.getPartner();
                Trade trade = trader.getTrade();
                if (trade == null || trade.isCancelled()) return;
                boolean econEnabled = plugin.getEconomyHandler().supportsEconomy();
                List<net.minecraft.world.item.ItemStack> items = (List<net.minecraft.world.item.ItemStack>)
                        ReflectUtils.getField(packet, "c");
                for (int i = 0; i < items.size(); i++) {
                    if(TradeUtils.isMiddle(i)) continue;
                    ItemStack slotDisplayItem = CraftItemStack.asBukkitCopy(items.get(i));
                    boolean otherTraderSlot = TradeUtils.isOtherTraderSlot(i);

                    if (otherTraderSlot && partner.getOfferedItems().size() > TradeUtils.convertOtherSlotToTradeIndex(i))
                            slotDisplayItem = partner.getOfferedItems().get(TradeUtils.convertOtherSlotToTradeIndex(i));
                    else if (trader.getOfferedItems().size() > TradeUtils.convertSlotToTradeIndex(i))
                            slotDisplayItem = trader.getOfferedItems().get(TradeUtils.convertSlotToTradeIndex(i));

                    net.minecraft.world.item.ItemStack display = CraftItemStack.asNMSCopy(slotDisplayItem);
                    items.set(i, display);
                }
                for (int i = 0; i < GuiTrade.SIZE; i++) if (i % 9 == 4) items.set(i, CraftItemStack.asNMSCopy(ItemUtils.createMenuGlass()));
                if (econEnabled) items.set(40, CraftItemStack.asNMSCopy(createMoneyButton(trader)));
                items.set(49, CraftItemStack.asNMSCopy(createAcceptButton(trade, trader)));
                ReflectUtils.setField(packet, "c", items);
            }
        };
    }

    private static ItemStack createAcceptButton(Trade trade, Trader trader) {
        Trader otherTrader = trader.getPartner();
        ItemStack acceptTradeButton = new ItemStack(Material.AIR);
        acceptTradeButton.setType(trade.getCouple().oneMeets(Trader::isAcceptedTrade) ? Material.GREEN_WOOL : Material.RED_WOOL);

        acceptTradeButton.setDisplayName("\u00a7aAccept trade");
        acceptTradeButton.clearLore();
        String lore;
        if(trader.isAcceptedTrade() && otherTrader.isAcceptedTrade()) lore = "\u00a77You have both accepted the trade.";
        else if(trader.isAcceptedTrade()) lore = "\u00a77You accepted the trade.";
        else if(otherTrader.isAcceptedTrade()) lore = "\u00a77" + otherTrader.getPlayer().getName() + " has accepted the trade.";
        else lore = "\u00a77No one accepted the trade.";
        acceptTradeButton.addLore(lore);
        acceptTradeButton.setAmount((trade.getSecondsUntilAccept() == -1 || !trade.isTradeAccepted()) ? 1 : trade.getSecondsUntilAccept() + 1);

        if(trader.isAcceptedTrade() && otherTrader.isAcceptedTrade()) acceptTradeButton.addGlint();

        return acceptTradeButton;
    }

    private static ItemStack createMoneyButton(Trader trader) {
        Trader otherTrader = trader.getPartner();
        ItemStack moneyButton = new ItemStack(Material.GOLD_INGOT);
        moneyButton
                .setDisplayName("\u00a7aMoney Offered")
                .addLore("")
                .addLore(String.format("\u00a7eYou have offered \u00a7c%s \u00a7ecoins.", trader.getOfferedCoins()))
                .addLore("")
                .addLore(String.format("\u00a7e%s has offered \u00a7d%s \u00a7ecoins.", otherTrader.getPlayer().getName(), otherTrader.getOfferedCoins()));
        return moneyButton;
    }

    public static void init(DTrade plugin) {
        new TradeView(plugin);
    }

}

package org.dtrade.packets;

import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dtrade.EconomyHandler;
import org.dtrade.gui.guis.TradeGui;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.ItemUtils;
import org.dtrade.util.ReflectUtils;
import org.dtrade.util.TradeUtils;

import java.lang.reflect.Field;
import java.util.List;

@ExtensionMethod({ItemUtils.class})
public class TradeView {

    private TradeView() {
        PacketHandler.getPacketHandler().subscribe(PacketPlayOutSetSlot.class, getSetSlotWriter());
        PacketHandler.getPacketHandler().subscribe(PacketPlayOutWindowItems.class, getWindowItemsWriter());
    }

    private PacketHandler.PacketWriteSubscriber<PacketPlayOutSetSlot> getSetSlotWriter() {
        return new PacketHandler.PacketWriteSubscriber<PacketPlayOutSetSlot>() {
            @Override @SneakyThrows
            public void onWrite(Player player, PacketPlayOutSetSlot packet) {
                Trader trader = Trader.getTrader(player);
                if(trader == null) return;
                Trade trade = trader.getTrade();
                if(trade == null || trade.isCancelled()) return;
                boolean econEnabled = EconomyHandler.getEconomyHandler().supportsEconomy();
                Field slotField = PacketPlayOutSetSlot.class.getDeclaredField("e");
                slotField.setAccessible(true);
                int slot = (int) slotField.get(packet);

                if (TradeUtils.isMiddle(slot)) {
                    System.out.println("set 1");
                    if(slot == 40 && econEnabled) ReflectUtils.setField(packet, "f", CraftItemStack.asNMSCopy(createMoneyButton(trade, trader)));
                    else if(slot == 49) ReflectUtils.setField(packet, "f", CraftItemStack.asNMSCopy(createAcceptButton(trade, trader)));
                    else if (slot % 9 == 4) ReflectUtils.setField(packet, "f", CraftItemStack.asNMSCopy(createMenuGlass()));
                    return;
                }



                ItemStack slotDisplayItem = new ItemStack(Material.AIR);

                int convertedSlot = TradeUtils.convertOtherSlotToTradeIndex(slot);
                if(convertedSlot < 0) {
                    return;
                }

                if (!TradeUtils.isOtherTraderSlot(slot) && trader.getOfferedItems().size() > convertedSlot)
                    slotDisplayItem = trader.getOfferedItems().get(convertedSlot);
                else if (trade.getCouple().other(trader).getOfferedItems().size() > convertedSlot)
                    slotDisplayItem = trade.getCouple().other(trader).getOfferedItems().get(convertedSlot);

                net.minecraft.world.item.ItemStack display = CraftItemStack.asNMSCopy(slotDisplayItem);

                ReflectUtils.setField(packet, "f", display);
            }
        };
    }

    private PacketHandler.PacketWriteSubscriber<PacketPlayOutWindowItems> getWindowItemsWriter() {
        return new PacketHandler.PacketWriteSubscriber<>() {
            @Override
            @SneakyThrows
            public void onWrite(Player player, PacketPlayOutWindowItems packet) {
                Trader trader = Trader.getTrader(player);
                if (trader == null) return;
                Trade trade = trader.getTrade();
                if (trade == null || trade.isCancelled()) return;
                boolean econEnabled = EconomyHandler.getEconomyHandler().supportsEconomy();
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
                for (int i = 0; i < TradeGui.SIZE; i++)
                    if (i % 9 == 4) items.set(i, CraftItemStack.asNMSCopy(createMenuGlass()));
                if (econEnabled) items.set(40, CraftItemStack.asNMSCopy(createMoneyButton(trade, trader)));
                items.set(49, CraftItemStack.asNMSCopy(createAcceptButton(trade, trader)));
                slotsField.set(packet, items);
            }
        };
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
        acceptTradeButton.setAmount(trade.getSecondsUntilAccept() == -1 ? 1 : trade.getSecondsUntilAccept() + 1);

        if(trader.isAcceptedTrade() && otherTrader.isAcceptedTrade()) acceptTradeButton.addGlint();

        return acceptTradeButton;
    }

    private static ItemStack createMoneyButton(Trade trade, Trader trader) {
        Trader otherTrader = trade.getCouple().other(trader);
        ItemStack moneyButton = new ItemStack(Material.GOLD_INGOT);
        moneyButton
                .setDisplayName("\u00a7aMoney Offered")
                .addLore("")
                .addLore(String.format("\u00a7eYou have offered \u00a7c%s \u00a7ecoins.", trader.getOfferedCoins()))
                .addLore("")
                .addLore(String.format("\u00a7e%s has offered \u00a7d%s \u00a7ecoins.", otherTrader.getPlayer().getName(), otherTrader.getOfferedCoins()));
        return moneyButton;
    }

    public static void init() {
        new TradeView();
    }

}

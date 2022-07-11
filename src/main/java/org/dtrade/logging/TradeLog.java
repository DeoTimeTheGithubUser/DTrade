package org.dtrade.logging;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_18_R2.CraftOfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.dtrade.trade.Trade;
import org.dtrade.trade.Trader;
import org.dtrade.util.Couple;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class TradeLog implements Serializable {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    @Getter
    private Date date;

    @Getter
    private Couple<OfflinePlayer> traders;
    @Getter
    private Couple<ItemStack[]> items;
    @Getter
    private Couple<Long> coins;

    public TradeLog(Date date, Couple<OfflinePlayer> traders, Couple<ItemStack[]> items, Couple<Long> coins) {
        this.date = date;
        this.traders = traders;
        this.items = items;
        this.coins = coins;
    }

    public String serialize() {
        JsonObject log = new JsonObject();

        log.addProperty("date", date.getTime());
        traders.both(p -> {
            JsonObject trader = new JsonObject();
            int index = traders.indexOf(p);
            trader.addProperty("uuid", p.getUniqueId().toString());
            trader.addProperty("items", index == 0? serializeItems(items.getFirst()) : serializeItems(items.getSecond()));
            trader.addProperty("coins", index == 0 ? coins.getFirst() : coins.getSecond());
            log.add("trader" + (index + 1), trader);
        });

        return log.toString();
    }

    public static TradeLog deserialize(JsonObject obj) {

        Date date = Date.from(Instant.ofEpochMilli(obj.get("date").getAsLong()));

        JsonObject trader1 = obj.getAsJsonObject("trader1");
        JsonObject trader2 = obj.getAsJsonObject("trader2");

        Couple<OfflinePlayer> players = Couple.of(
                Bukkit.getOfflinePlayer(UUID.fromString(trader1.get("uuid").getAsString())),
                Bukkit.getOfflinePlayer(UUID.fromString(trader2.get("uuid").getAsString()))
        );
        Couple<ItemStack[]> items = Couple.of(
                deserializeItems(trader1.get("items").getAsString()),
                deserializeItems(trader2.get("items").getAsString())
        );
        Couple<Long> coins = Couple.of(
                trader1.get("coins").getAsLong(),
                trader2.get("coins").getAsLong()
        );
        return new TradeLog(date, players, items, coins);
    }

    public static @NotNull TradeLog createLog(@NotNull Trade trade) {
        Date date = Date.from(Instant.now());

        Trader trader1 = trade.getCouple().getFirst();
        Trader trader2 = trade.getCouple().getSecond();

        Couple<OfflinePlayer> players = Couple.of(trader1.getPlayer(), trader2.getPlayer());
        Couple<ItemStack[]> items = Couple.of(trader1.getOfferedItems().toArray(ItemStack[]::new), trader2.getOfferedItems().toArray(ItemStack[]::new));
        Couple<Long> coins = Couple.of(trader1.getOfferedCoins(), trader2.getOfferedCoins());
        return new TradeLog(
                date,
                players,
                items,
                coins
        );
    }

    @SneakyThrows
    private static String serializeItems(ItemStack[] items) {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        BukkitObjectOutputStream output = new BukkitObjectOutputStream(byteOutput);
        output.writeObject(items);
        return encoder.encodeToString(byteOutput.toByteArray());
    }

    @SneakyThrows
    private static ItemStack[] deserializeItems(String serialized) {
        ByteArrayInputStream byteInput = new ByteArrayInputStream(decoder.decode(serialized));
        BukkitObjectInputStream input = new BukkitObjectInputStream(byteInput);
        return (ItemStack[]) input.readObject();
    }
}

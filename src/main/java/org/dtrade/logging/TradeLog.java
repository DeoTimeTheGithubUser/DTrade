package org.dtrade.logging;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    private UUID trader1;
    private UUID trader2;

    private String trader1Items;
    private String trader2Items;

    private long trader1Coins;
    private long trader2Coins;

    public TradeLog(Date date, UUID trader1, UUID trader2, ItemStack[] trader1Items, ItemStack[] trader2Items, long trader1Coins, long trader2Coins) {
        this.date = date;
        this.trader1 = trader1;
        this.trader2 = trader2;
        this.trader1Items = serializeItems(trader1Items);
        this.trader2Items = serializeItems(trader2Items);
        this.trader1Coins = trader1Coins;
        this.trader2Coins = trader2Coins;
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public Couple<OfflinePlayer> getPlayers() {
        return Couple.of(Bukkit.getOfflinePlayer(trader1), Bukkit.getOfflinePlayer(trader2));
    }

    public Couple<ItemStack[]> getItems() {
        return Couple.of(
                deserializeItems(trader1Items),
                deserializeItems(trader2Items)
        );
    }

    public Couple<Long> getOfferedCoins() {
        return Couple.of(trader1Coins, trader2Coins);
    }

    public static @NotNull TradeLog createLog(@NotNull Trade trade) {
        Date date = Date.from(Instant.now());

        Trader trader1 = trade.getCouple().getFirst();
        Trader trader2 = trade.getCouple().getSecond();

        UUID uuid1 = trader1.getPlayer().getUniqueId();
        UUID uuid2 = trader2.getPlayer().getUniqueId();

        ItemStack[] trader1Items = trader1.getOfferedItems().toArray(ItemStack[]::new);
        ItemStack[] trader2Items = trader2.getOfferedItems().toArray(ItemStack[]::new);

        long trader1Coins = trade.getCouple().getFirst().getOfferedCoins();
        long trader2Coins = trade.getCouple().getSecond().getOfferedCoins();


        return new TradeLog(
                date,
                uuid1,
                uuid2,
                trader1Items,
                trader2Items,
                trader1Coins,
                trader2Coins
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

package org.dtrade.packets;

import lombok.SneakyThrows;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftSign;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;
import org.dtrade.util.ReflectUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

public class SignInput {

    private final Plugin plugin;
    private final Player player;
    private final String[] prompt;
    private final CompletableFuture<String[]> cb;

    private boolean valid = true;
    private final Listener validator = new Listener() {
        @EventHandler
        private void onOpen(InventoryOpenEvent event) {
            valid = false;
        }

        @EventHandler
        private void onClose(InventoryCloseEvent event) {
            valid = false;
        }
    };

    private SignInput(Plugin plugin, Player player, String[] prompt, CompletableFuture<String[]> cb) {
        this.plugin = plugin;
        this.player = player;
        this.prompt = prompt;
        this.cb = cb;
        init();
    }

    @SneakyThrows
    private void init() {

        plugin.getServer().getPluginManager().registerEvents(validator, plugin);

        int
                x = player.getLocation().getBlockX(),
                y = 1,
                z = player.getLocation().getBlockZ();
        BlockPosition pos = new BlockPosition(x, y, z);
        PacketPlayOutBlockChange blockPacket = new PacketPlayOutBlockChange(((CraftWorld) player.getWorld()).getHandle(), pos);

        IBlockData data = CraftMagicNumbers.getBlock(Material.OAK_WALL_SIGN).n();
        Field blockData = PacketPlayOutBlockChange.class.getDeclaredField("b");
        blockData.setAccessible(true);
        blockData.set(blockPacket, data);

        IChatBaseComponent[] comp = CraftSign.sanitizeLines(this.prompt);
        TileEntitySign sign = new TileEntitySign(new BlockPosition(x, y, z), data);
        ReflectUtils.setField(sign, "d", comp);
        ReflectUtils.setField(sign, "e", comp);

        PacketPlayOutOpenSignEditor signPacket = new PacketPlayOutOpenSignEditor(pos);
        ((CraftPlayer) player).getHandle().b.a(blockPacket);
        ((CraftPlayer) player).getHandle().b.a(sign.c());
        ((CraftPlayer) player).getHandle().b.a(signPacket);
        PacketHandler.getPacketHandler().subscribe(PacketPlayInUpdateSign.class, (PacketHandler.PacketReadSubscriber<PacketPlayInUpdateSign>) (player, packet) -> {
            if(valid) Bukkit.getScheduler().runTask(plugin, () -> cb.complete(packet.c()));
            valid = false;
        });
    }

    public static @NotNull CompletableFuture<String[]> requestSignInput (@NotNull Plugin plugin, Player player, String[] prompt) {
        CompletableFuture<String[]> future = new CompletableFuture<>();
        new SignInput(plugin, player, prompt, future);
        return future;
    }

}

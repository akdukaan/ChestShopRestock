package com.birdflop.chestshoprestock;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

public class ListenerTransaction implements Listener {

    @EventHandler
    public void onTransaction(TransactionEvent event) {
        Sign sign = event.getSign();
        String price = ChestShopSign.getPrice(sign);
        if (!price.contains("B")) return;
        String player = event.getOwnerAccount().getUuid().toString();
        String item = ChestShopSign.getItem(sign);
        Location loc = event.getSign().getLocation();
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        ChestShopRestock.database.addEntry(player, item, world, x, y, z);
    }

    @Nullable
    public String getPriceLine(Location location) {
        if (!location.isChunkLoaded()) return null;
        Block block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        BlockState state = block.getState();
        if (!(state instanceof Sign)) return null;
        Sign sign = (Sign) state;
        return ChestShopSign.getPrice(sign);
    }
}

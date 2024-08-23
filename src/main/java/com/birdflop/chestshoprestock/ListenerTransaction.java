package com.birdflop.chestshoprestock;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ListenerTransaction implements Listener {
    private String cachedPlayer = null;
    private String cachedItem = null;
    private String cachedWorld = null;
    private Integer cachedX = null;
    private Integer cachedY = null;
    private Integer cachedZ = null;

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

        if (isCached(player, item, world, x, y, z)) return;
        ChestShopRestock.database.addEntry(player, item, world, x, y, z);
    }

    private boolean isCached(String player, String item, String world, int x, int y, int z) {
        if (!player.equals(cachedPlayer)) return false;
        if (!item.equals(cachedItem)) return false;
        if (!world.equals(cachedWorld)) return false;
        if (x != cachedX) return false;
        if (y != cachedY) return false;
        if (z != cachedZ) return false;

        cachedPlayer = player;
        cachedItem = item;
        cachedWorld = world;
        cachedX = x;
        cachedY = y;
        cachedZ = z;
        return true;
    }
}

package com.birdflop.chestshoprestock;

import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ListenerShopDeleted implements Listener {

    @EventHandler
    public void onShopDeleted(ShopDestroyedEvent event) {
        Location loc = event.getSign().getLocation();
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        ChestShopRestock.database.removeEntry(world, x, y, z);
    }
}

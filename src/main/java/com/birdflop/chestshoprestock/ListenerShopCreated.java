package com.birdflop.chestshoprestock;

import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ListenerShopCreated implements Listener {

    @EventHandler
    public void onShopCreated(ShopCreatedEvent event) {
        String price = event.getSignLine((short) 2);
        if (!price.contains("B")) return;
        String player = event.getPlayer().getUniqueId().toString();
        String item = event.getSignLine((short) 3);
        Location loc = event.getSign().getLocation();
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        ChestShopRestock.database.addEntry(player, item, world, x, y, z);
    }
}

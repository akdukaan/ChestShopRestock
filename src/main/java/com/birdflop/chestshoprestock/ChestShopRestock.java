package com.birdflop.chestshoprestock;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestShopRestock extends JavaPlugin {

    public static ChestShopRestock plugin;
    public static Database database;

    @Override
    public void onEnable() {
        plugin = this;
        database = new Database(this);

        Lang.reload();

        getServer().getPluginManager().registerEvents(new ListenerShopCreated(), this);
        getServer().getPluginManager().registerEvents(new ListenerShopDeleted(), this);
        getServer().getPluginManager().registerEvents(new ListenerTransaction(), this);

        getCommand("restock").setExecutor(new CommandRestock());
        getCommand("csrestock").setExecutor(new CommandCsrestock());

        new Metrics(this, 22906);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

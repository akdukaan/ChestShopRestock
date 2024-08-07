package com.birdflop.chestshoprestock;

import com.google.common.collect.ImmutableMap;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

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
        try {
            addCustomMetrics();
        } catch (Exception ignored) {

        }

    }

    private void addCustomMetrics() {
        Metrics bStats = new Metrics(this, 22906);
        String serverVersion = getServer().getBukkitVersion().split("-")[0];

        bStats.addCustomChart(createStaticDrilldownStat("version_mc_plugin", serverVersion, getDescription().getVersion()));
        bStats.addCustomChart(createStaticDrilldownStat("version_plugin_mc", getDescription().getVersion(), serverVersion));

        bStats.addCustomChart(createStaticDrilldownStat("version_brand_plugin", getServer().getName(), getDescription().getVersion()));
        bStats.addCustomChart(createStaticDrilldownStat("version_plugin_brand", getDescription().getVersion(), getServer().getName()));

        bStats.addCustomChart(createStaticDrilldownStat("version_mc_brand", serverVersion, getServer().getName()));
        bStats.addCustomChart(createStaticDrilldownStat("version_brand_mc", getServer().getName(), serverVersion));
    }

    private static DrilldownPie createStaticDrilldownStat(String statId, String value1, String value2) {
        final Map<String, Map<String, Integer>> map = ImmutableMap.of(value1, ImmutableMap.of(value2, 1));
        return new DrilldownPie(statId, () -> map);
    }
}

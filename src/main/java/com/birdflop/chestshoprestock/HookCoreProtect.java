package com.birdflop.chestshoprestock;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class HookCoreProtect {

    public static void logTransaction(Player player, Location location) {
        CoreProtectAPI cp = getCoreProtect();
        if (cp == null) return;
        cp.logContainerTransaction(player.getName(), location);
    }

    private static CoreProtectAPI getCoreProtect() {
        // Check that CoreProtect is loaded
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");
        if (!(plugin instanceof CoreProtect)) return null;

        // Check that the API is enabled
        CoreProtectAPI coreProtect = ((CoreProtect) plugin).getAPI();
        if (!coreProtect.isEnabled()) return null;

        // Check that a compatible version of the API is loaded
        if (coreProtect.APIVersion() < 10) return null;

        return coreProtect;
    }
}

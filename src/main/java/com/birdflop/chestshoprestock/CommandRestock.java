package com.birdflop.chestshoprestock;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.ItemUtil;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandRestock implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            Lang.sendMessage(commandSender, Lang.COMMAND_PLAYERS_ONLY);
            return true;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("chestshoprestock.command.restock")) {
            Lang.sendMessage(player, Lang.COMMAND_NO_PERMISSION);
            return true;
        }
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (int i = 0; i < contents.length; i++) {
            moveStack(player, i);
        }
        Lang.sendMessage(player, Lang.RESTOCK_SUCCESS);
        return true;
    }

    public void moveStack(Player player, int inventoryIndex) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] storage = playerInventory.getStorageContents();
        ItemStack content = storage[inventoryIndex];
        if (content == null) return;
        String itemName = ItemUtil.getSignName(content);
        String playerUuid = player.getUniqueId().toString();
        ArrayList<Location> locations = ChestShopRestock.database.getLocations(playerUuid, itemName);

        for (Location location : locations) {
            Container container = getContainer(location, player);
            if (container != null) {
                HashMap<Integer, ItemStack> couldntStore = container.getInventory().addItem(content);
                storage[inventoryIndex] = couldntStore.get(0);
                playerInventory.setStorageContents(storage);
            }
        }
    }

    /**
     *
     * @param location get the container that has a shop sign at this location
     * @param player verify that the shop is owned by the player
     * @return
     */
    @Nullable
    public Container getContainer(Location location, @Nullable Player player) {
        if (!location.isChunkLoaded()) {
            if (player == null) return null;
            if (player.hasPermission("chestshoprestock.command.restock.loadchunks")) {
                location.getChunk().load(false);
            }
            if (!location.isChunkLoaded()) return null;
        }
        Block block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (player != null) {
            BlockState state = block.getState();
            if (!(state instanceof Sign)) return null;
            Sign sign = (Sign) state;
            if (!ChestShopSign.isOwner(player, sign)) return null;
        }

        return uBlock.findConnectedContainer(block);
    }
}

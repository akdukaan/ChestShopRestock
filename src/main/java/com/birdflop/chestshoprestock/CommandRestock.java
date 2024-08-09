package com.birdflop.chestshoprestock;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.ItemUtil;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
        ItemStack[] contentAfter = player.getInventory().getStorageContents();
        if (Arrays.equals(contents, contentAfter)) {
            Lang.sendMessage(player, Lang.NOTHING_TO_RESTOCK);
            return true;
        }
        Lang.sendMessage(player, Lang.RESTOCK_SUCCESS);
        return true;
    }

    public void moveStack(Player player, int inventoryIndex) {
        // Check that an ItemStack exists at the location
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] storage = playerInventory.getStorageContents();
        ItemStack content = storage[inventoryIndex];
        if (content == null) return;

        // Verify that this item can be on signs
        String itemName;
        try {
            itemName = ItemUtil.getSignName(content);
        } catch (IllegalArgumentException ignored) {
            return;
        }

        String playerUuid = player.getUniqueId().toString();
        ArrayList<Location> locations = ChestShopRestock.database.getLocations(playerUuid, itemName);

        // Move the stack to whatever chestshop it can find
        for (Location location : locations) {
            Container container = getContainer(location, player);
            if (container != null) {
                HookCoreProtect.logTransaction(player, container.getLocation());
                HashMap<Integer, ItemStack> couldntStore = container.getInventory().addItem(content);
                storage[inventoryIndex] = couldntStore.get(0);
                playerInventory.setStorageContents(storage);
            }
        }
    }

    /**
     *
     * @param location get the container that has a shop sign at this location
     * @param player verify that the shop is owned by them and check their permission to see if we should load chunks
     * @return The container of the shop block
     */
    @Nullable
    public Container getContainer(Location location, Player player) {
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
            if (player.hasPermission("chestshoprestock.command.restock.loadchunks")) {
                loadConnectedContainer(sign);
            }
        }

        return uBlock.findConnectedContainer(block);
    }

    /**
     * Assures that the chest connected to the sign will be loaded
     * @param sign a loaded Sign
     */
    public void loadConnectedContainer(Sign sign) {
        BlockFace signFace = null;
        BlockData data = sign.getBlockData();
        if (data instanceof WallSign) {
            signFace = ((WallSign) data).getFacing().getOppositeFace();
        }
        Location location = sign.getLocation();

        if (signFace != null) {
            Block faceBlock = location.clone().add(signFace.getModX(), signFace.getModY(), signFace.getModZ()).getBlock();
            faceBlock.getChunk().load(false);
            if (uBlock.couldBeShopContainer(faceBlock)) return;
        }

        for (BlockFace bf : uBlock.SHOP_FACES) {
            if (bf != signFace) {
                Block faceBlock = location.clone().add(bf.getModX(), bf.getModY(), bf.getModZ()).getBlock();
                faceBlock.getChunk().load(false);
                if (uBlock.couldBeShopContainer(faceBlock)) return;
            }
        }
    }
}

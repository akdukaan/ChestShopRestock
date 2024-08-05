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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CommandRestock implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        if (!commandSender.hasPermission("chestshoprestock.command.restock")) {
            player.sendMessage("No permission!");
            return true;
        }
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (ItemStack content : contents) {
            dumpContents(player, content);
        }
        player.sendMessage("Restocked!");
        return true;
    }

    public void dumpContents(Player player, ItemStack content) {
        if (content == null) return;
        String itemName = ItemUtil.getSignName(content);
        String playerUuid = player.getUniqueId().toString();
        ArrayList<Location> locations = ChestShopRestock.database.getLocations(playerUuid, itemName);
        for (Location location : locations) {
            Container container = getContainer(location, player);
            if (container != null) {
                moveItems(player, container, content);
            }
        }
    }

    public void moveItems(Player player, Container container, ItemStack itemToMove) {
        Inventory inv = container.getInventory();
        for (ItemStack existingContainerItem : inv.getStorageContents()) {
            if (itemToMove.getAmount() <= 0) return;
            if (existingContainerItem != null && existingContainerItem.isSimilar(itemToMove)) {
                int maxStackSize = itemToMove.getMaxStackSize();
                int space = maxStackSize - existingContainerItem.getAmount();
                if (space > 0) {
                    int itemStackAmount = itemToMove.getAmount();
                    // Calculate how much to add to this itemstack
                    int amountToMove = Math.min(space, itemStackAmount);
                    existingContainerItem.setAmount(existingContainerItem.getAmount() + amountToMove);
                    // Update itemstack to reflect how much more we need to move
                    itemToMove.setAmount(itemStackAmount - amountToMove);
                    removeFromPlayer(player, itemToMove, amountToMove);
                }
            }
        }
        int emptySlot = container.getInventory().firstEmpty();
        if (emptySlot >= 0) {
            container.getInventory().setItem(emptySlot, itemToMove);
            removeFromPlayer(player, itemToMove, itemToMove.getAmount());
        }
    }

    public void removeFromPlayer(Player player, ItemStack stack, int removeAmount) {
        for (ItemStack existingItem : player.getInventory().getStorageContents()) {
            if (removeAmount <= 0) return;
            if (existingItem != null && existingItem.isSimilar(stack)) {
                int existingAmount = existingItem.getAmount();
                // Calculate how much to remove from this itemstack
                int amountToRemove = Math.min(existingAmount, removeAmount);
                existingItem.setAmount(existingAmount - amountToRemove);
                // Calculate how much more we need to remove
                removeAmount -= amountToRemove;
            }
        }
    }

    /**
     *
     * @param location get the container that has a shop sign at this location
     * @param player verify that the shop is owned by the player
     * @return
     */
    public Container getContainer(Location location, @Nullable Player player) {
        if (!location.isChunkLoaded()) return null;
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

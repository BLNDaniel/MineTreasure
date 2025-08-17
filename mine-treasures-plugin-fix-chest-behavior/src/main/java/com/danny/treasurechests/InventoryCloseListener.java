package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryCloseListener implements Listener {

    private final TreasureChestManager treasureChestManager;
    private final JavaPlugin plugin;
    private final DisplayManager displayManager;

    public InventoryCloseListener(TreasureChestManager treasureChestManager, JavaPlugin plugin, DisplayManager displayManager) {
        this.treasureChestManager = treasureChestManager;
        this.plugin = plugin;
        this.displayManager = displayManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Location location = treasureChestManager.getOpenInventoryLocation(player.getUniqueId());

        // Check if the closed inventory is a treasure chest
        if (location == null || !treasureChestManager.isTreasureChest(location)) {
            treasureChestManager.removeOpenInventory(player.getUniqueId());
            return;
        }

        Inventory inventory = event.getInventory();
        boolean isEmpty = true;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                isEmpty = false;
                break;
            }
        }

        if (isEmpty) {
            // The chest is empty, remove it
            TreasureChestManager.TreasureChestData chestData = treasureChestManager.getChestDataAt(location);
            if (chestData != null) {
                Entity displayEntity = plugin.getServer().getEntity(chestData.displayId());
                if (displayEntity != null) {
                    displayEntity.remove();
                }
            }

            location.getBlock().setType(Material.AIR);
            treasureChestManager.removeTreasureChest(location);
        }

        // Clean up the tracking map
        treasureChestManager.removeOpenInventory(player.getUniqueId());
    }
}

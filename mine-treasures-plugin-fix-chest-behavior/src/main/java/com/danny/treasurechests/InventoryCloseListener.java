package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

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
        UUID playerId = player.getUniqueId();

        // Check if this player was viewing a treasure chest inventory
        Location location = treasureChestManager.getOpenInventoryLocation(playerId);
        if (location == null) {
            return; // This was not a treasure chest inventory we are tracking.
        }

        // IMPORTANT: Un-track the player *immediately*.
        // This prevents double-processing and issues if the player opens another chest quickly.
        treasureChestManager.removeOpenInventory(playerId);

        // Now, check if the location still holds a treasure chest.
        // It might have been removed by another process in the meantime.
        if (!treasureChestManager.isTreasureChest(location)) {
            return;
        }

        // Check if the inventory is empty using the reliable isEmpty() method
        if (event.getInventory().isEmpty()) {
            // The chest is empty, remove it using the DisplayManager to handle animations
            displayManager.despawnTreasure(location);
        }
    }
}

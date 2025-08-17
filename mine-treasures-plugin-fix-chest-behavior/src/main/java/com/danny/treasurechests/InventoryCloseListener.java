package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

        Location location = treasureChestManager.getOpenInventoryLocation(playerId);
        if (location == null) {
            return;
        }

        treasureChestManager.removeOpenInventory(playerId);

        // Delay the check by one tick to ensure inventory state is updated.
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!treasureChestManager.isTreasureChest(location)) {
                    return;
                }

                // We need to get the inventory again, as the event's inventory might not be safe to use across ticks.
                Inventory inventory = treasureChestManager.getInventoryAt(location);
                if (inventory == null) {
                    return;
                }

                if (inventory.isEmpty()) {
                    displayManager.despawnTreasure(location);
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}

package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryClickListener implements Listener {

    private final TreasureChestManager treasureChestManager;
    private final DisplayManager displayManager;
    private final JavaPlugin plugin;

    public InventoryClickListener(TreasureChestManager treasureChestManager, DisplayManager displayManager, JavaPlugin plugin) {
        this.treasureChestManager = treasureChestManager;
        this.displayManager = displayManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        Location location = treasureChestManager.getOpenInventoryLocation(player.getUniqueId());
        if (location == null) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        Inventory treasureInventory = treasureChestManager.getInventoryAt(location);
        if (!clickedInventory.equals(treasureInventory)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (treasureInventory.isEmpty()) {
                    displayManager.despawnTreasure(location);
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}

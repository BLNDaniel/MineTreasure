package com.danny.treasurechests;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        final Player player = (Player) event.getPlayer();
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

                Inventory inventory = treasureChestManager.getInventoryAt(location);
                if (inventory == null) {
                    return;
                }

                if (player.isOp()) {
                    player.sendMessage(ChatColor.YELLOW + "[Debug] Checking inventory for despawn at " + location.toVector());
                    player.sendMessage(ChatColor.YELLOW + "[Debug] Inventory.isEmpty() -> " + inventory.isEmpty());
                    if (!inventory.isEmpty()) {
                        player.sendMessage(ChatColor.YELLOW + "[Debug] Contents:");
                        for (ItemStack item : inventory.getContents()) {
                            if (item != null && item.getType() != Material.AIR) {
                                player.sendMessage(ChatColor.GRAY + "- " + item.getType().name() + " x" + item.getAmount());
                            }
                        }
                    }
                }

                if (inventory.isEmpty()) {
                    displayManager.despawnTreasure(location);
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}

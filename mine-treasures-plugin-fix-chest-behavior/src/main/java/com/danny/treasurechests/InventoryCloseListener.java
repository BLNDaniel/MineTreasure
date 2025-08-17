package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

public class InventoryCloseListener implements Listener {

    private final TreasureChestManager treasureChestManager;
    private final TreasureChests plugin;
    private final DisplayManager displayManager;

    public InventoryCloseListener(TreasureChestManager treasureChestManager, TreasureChests plugin, DisplayManager displayManager) {
        this.treasureChestManager = treasureChestManager;
        this.plugin = plugin;
        this.displayManager = displayManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        UUID playerId = player.getUniqueId();

        Location location = treasureChestManager.getOpenInventoryLocation(playerId);
        if (location == null) {
            return;
        }

        treasureChestManager.removeOpenInventory(playerId);

        // Apply rewards
        plugin.getRewardManager().applyRewards(player, location);
    }
}

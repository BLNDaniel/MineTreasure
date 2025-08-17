package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class BarrierInteractListener implements Listener {

    private final TreasureChestManager treasureChestManager;

    public BarrierInteractListener(TreasureChestManager treasureChestManager) {
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        if (event.getClickedBlock().getType() != Material.BARRIER) {
            return;
        }

        Location location = event.getClickedBlock().getLocation();
        if (!treasureChestManager.isTreasureChest(location)) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        Inventory barrelInventory = treasureChestManager.getInventoryAt(location);

        if (barrelInventory != null) {
            treasureChestManager.setInventoryOpen(player.getUniqueId(), location);
            player.openInventory(barrelInventory);
        }
    }
}

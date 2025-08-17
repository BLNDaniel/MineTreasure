package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        TreasureChestManager.TreasureChestData chestData = treasureChestManager.getChestDataAt(location);
        if (chestData == null) return;

        Player player = event.getPlayer();

        // Create a virtual barrel inventory
        Inventory barrelInventory = Bukkit.createInventory(null, InventoryType.BARREL, chestData.tier().getDisplayName());

        // Place items in random slots
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < barrelInventory.getSize(); i++) {
            slots.add(i);
        }
        Collections.shuffle(slots);

        for (int i = 0; i < chestData.items().size(); i++) {
            if (i >= slots.size()) break;
            barrelInventory.setItem(slots.get(i), chestData.items().get(i));
        }

        player.openInventory(barrelInventory);
    }
}

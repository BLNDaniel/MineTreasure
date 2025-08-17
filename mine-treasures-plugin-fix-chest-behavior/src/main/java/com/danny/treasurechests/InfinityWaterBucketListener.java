package com.danny.treasurechests;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class InfinityWaterBucketListener implements Listener {

    private final TreasureChests plugin;

    public InfinityWaterBucketListener(TreasureChests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.WATER_BUCKET && item.hasItemMeta()) {
            if (item.getItemMeta().getPersistentDataContainer().has(plugin.getNamespacedKey("infinity_water_bucket"), PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
                Block clickedBlock = event.getClickedBlock();
                if (clickedBlock != null) {
                    Block targetBlock = clickedBlock.getRelative(event.getBlockFace());
                    targetBlock.setType(Material.WATER);
                }
            }
        }
    }
}

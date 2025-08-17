package com.danny.treasurechests;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class InfinityWaterBucketListener implements Listener {

    private final TreasureChests plugin;

    public InfinityWaterBucketListener(TreasureChests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        ItemStack item = event.getItemStack();
        if (item != null && item.getType() == Material.WATER_BUCKET && item.hasItemMeta()) {
            if (item.getItemMeta().getPersistentDataContainer().has(plugin.getNamespacedKey("infinity_water_bucket"), PersistentDataType.BOOLEAN)) {
                event.setItemStack(item);
            }
        }
    }
}

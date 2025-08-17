package com.danny.treasurechests;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final TreasureChests plugin;
    private final TreasureChestManager treasureChestManager;

    public BlockPlaceListener(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Only track blocks that are allowed to drop treasure chests
        java.util.List<String> allowedBlocks = plugin.getConfig().getStringList("allowed-blocks");
        if (allowedBlocks.contains(event.getBlockPlaced().getType().name())) {
            treasureChestManager.addPlayerPlacedBlock(event.getBlock().getLocation());
        }
    }
}

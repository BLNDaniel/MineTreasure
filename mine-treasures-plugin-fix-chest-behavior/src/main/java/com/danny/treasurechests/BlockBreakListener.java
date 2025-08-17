package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final TreasureChests plugin;
    private final LootManager lootManager;
    private final TreasureChestManager treasureChestManager;

    public BlockBreakListener(TreasureChests plugin, LootManager lootManager, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.lootManager = lootManager;
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final org.bukkit.entity.Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        // Check if the block was placed by a player
        if (treasureChestManager.isPlayerPlacedBlock(location)) {
            treasureChestManager.removePlayerPlacedBlock(location); // Clean up the entry
            return;
        }

        // Check if the block is in the allowed list
        java.util.List<String> allowedBlocks = plugin.getConfig().getStringList("allowed-blocks");
        if (!allowedBlocks.contains(event.getBlock().getType().name())) {
            return;
        }

        // Check the drop chance
        double dropChance = plugin.getConfig().getDouble("drop-chance");
        if (new java.util.Random().nextDouble() >= dropChance) {
            return;
        }

        // Prevent the block from dropping its normal items
        event.setDropItems(false);

        // Asynchronously calculate the loot
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final LootManager.LootResult lootResult = lootManager.calculateRandomLoot();

            // If loot was successfully calculated, schedule the spawning back on the main thread
            if (lootResult != null && !lootResult.getItems().isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // Spawn the treasure chest and custom model
                    plugin.getDisplayManager().spawnTreasure(location, lootResult, player);

                    // Send feedback to the server
                    if (plugin.getConfig().getBoolean("broadcast-message-toggle", true)) {
                        String message = plugin.getConfig().getString("broadcast-message", "&e%player% found a %tier% treasure chest!");
                        String tierName = lootResult.getTier().getDisplayName();

                        message = message.replace("%player%", player.getName()).replace("%tier%", tierName);
                        Bukkit.broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
                    }

                    // Play sound based on tier settings
                    SoundInfo soundInfo = lootResult.getTier().getSoundInfo();
                    String soundName = soundInfo.getName();
                    try {
                        if (soundInfo.shouldBroadcast()) {
                            for (org.bukkit.entity.Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayer.playSound(onlinePlayer.getLocation(), soundName, 1.0f, 1.0f);
                            }
                        } else {
                            player.playSound(player.getLocation(), soundName, 1.0f, 1.0f);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Ein Fehler ist beim Abspielen des Sounds '" + soundName + "' aufgetreten. Ist der Sound-Name gültig?");
                    }
                });
            }
        });
    }
}

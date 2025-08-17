package com.danny.treasurechests;

import org.bukkit.plugin.java.JavaPlugin;

public class TreasureChests extends JavaPlugin {

    private LootManager lootManager;
    private TreasureChestManager treasureChestManager;
    private DisplayManager displayManager;

    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one is not present
        saveDefaultConfig();

        // Initialize managers and handlers
        this.lootManager = new LootManager(this);
        this.treasureChestManager = new TreasureChestManager();
        this.displayManager = new DisplayManager(this, treasureChestManager);

        // Load loot tables from config
        this.lootManager.loadLootTables();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, lootManager, treasureChestManager), this);
        getServer().getPluginManager().registerEvents(new BarrierInteractListener(treasureChestManager), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this, treasureChestManager), this);

        getLogger().info("TreasureChests wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TreasureChests wurde deaktiviert!");
    }

    public DisplayManager getDisplayManager() {
        return displayManager;
    }
}

package com.danny.treasurechests;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class TreasureChests extends JavaPlugin {

    private LootManager lootManager;
    private TreasureChestManager treasureChestManager;
    private DisplayManager displayManager;
    private MessageManager messageManager;
    private LuckBoosterManager luckBoosterManager;
    private ItemManager itemManager;
    private RewardManager rewardManager;

    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one is not present
        saveDefaultConfig();

        // Initialize managers and handlers
        this.messageManager = new MessageManager(this);
        this.lootManager = new LootManager(this);
        this.treasureChestManager = new TreasureChestManager();
        this.displayManager = new DisplayManager(this, treasureChestManager);
        this.luckBoosterManager = new LuckBoosterManager(this);
        this.itemManager = new ItemManager(this);
        this.rewardManager = new RewardManager(this);

        // Load loot tables from config
        this.lootManager.loadLootTables();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, lootManager, treasureChestManager), this);
        getServer().getPluginManager().registerEvents(new BarrierInteractListener(treasureChestManager), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this, treasureChestManager), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(treasureChestManager, this, displayManager), this);
        getServer().getPluginManager().registerEvents(new LuckBoosterListener(this), this);

        // Register commands
        getCommand("givetreasureitem").setExecutor(new GiveTreasureItemCommand(this));

        getLogger().info(messageManager.getPlainMessage("plugin-enabled"));
    }

    @Override
    public void onDisable() {
        getLogger().info(messageManager.getPlainMessage("plugin-disabled"));
    }

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public LuckBoosterManager getLuckBoosterManager() {
        return luckBoosterManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(this, key);
    }
}

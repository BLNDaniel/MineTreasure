package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TreasureChestManager {

    public record TreasureChestData(LootTier tier, List<ItemStack> items, UUID displayId) {}

    private final Map<Location, TreasureChestData> treasureChests = new HashMap<>();
    private final Map<Location, Inventory> treasureChestInventories = new HashMap<>();
    private final Set<Location> playerPlacedBlocks = new HashSet<>();
    private final Map<UUID, Location> openInventories = new HashMap<>();
    private final Set<Location> despawningChests = new HashSet<>();


    public void addTreasureChest(Location location, TreasureChestData data) {
        treasureChests.put(location, data);

        // Create and populate the inventory
        Inventory barrelInventory = Bukkit.createInventory(null, InventoryType.BARREL, ChatColor.translateAlternateColorCodes('&', data.tier().getDisplayName()));

        // Place items in random slots
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < barrelInventory.getSize(); i++) {
            slots.add(i);
        }
        Collections.shuffle(slots);

        for (int i = 0; i < data.items().size(); i++) {
            if (i >= slots.size()) break;
            barrelInventory.setItem(slots.get(i), data.items().get(i));
        }
        treasureChestInventories.put(location, barrelInventory);
    }

    public void removeTreasureChest(Location location) {
        treasureChests.remove(location);
        treasureChestInventories.remove(location);
        despawningChests.remove(location);
    }

    public TreasureChestData getChestDataAt(Location location) {
        return treasureChests.get(location);
    }

    public Inventory getInventoryAt(Location location) {
        return treasureChestInventories.get(location);
    }

    public boolean isTreasureChest(Location location) {
        return treasureChests.containsKey(location);
    }

    public void addPlayerPlacedBlock(Location location) {
        playerPlacedBlocks.add(location);
    }

    public void removePlayerPlacedBlock(Location location) {
        playerPlacedBlocks.remove(location);
    }

    public boolean isPlayerPlacedBlock(Location location) {
        return playerPlacedBlocks.contains(location);
    }

    public void setInventoryOpen(UUID playerId, Location location) {
        openInventories.put(playerId, location);
    }

    public Location getOpenInventoryLocation(UUID playerId) {
        return openInventories.get(playerId);
    }

    public void removeOpenInventory(UUID playerId) {
        openInventories.remove(playerId);
    }

    public boolean isDespawning(Location location) {
        return despawningChests.contains(location);
    }

    public void setDespawning(Location location) {
        despawningChests.add(location);
    }
}

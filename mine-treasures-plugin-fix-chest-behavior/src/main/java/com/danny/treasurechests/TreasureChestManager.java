package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TreasureChestManager {

    public record TreasureChestData(LootTier tier, List<ItemStack> items, UUID displayId) {}

    private final Map<Location, TreasureChestData> treasureChests = new HashMap<>();
    private final Set<Location> playerPlacedBlocks = new HashSet<>();

    public void addTreasureChest(Location location, TreasureChestData data) {
        treasureChests.put(location, data);
    }

    public void removeTreasureChest(Location location) {
        treasureChests.remove(location);
    }

    public TreasureChestData getChestDataAt(Location location) {
        return treasureChests.get(location);
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
}

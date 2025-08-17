package com.danny.treasurechests;

import org.bukkit.Material;

public class LootItem {

    private final Material material;
    private final String customItem;
    private final String amount;
    private final double chance;

    public LootItem(org.bukkit.Material material, String customItem, String amount, double chance) {
        this.material = material;
        this.customItem = customItem;
        this.amount = amount;
        this.chance = chance;
    }

    public Material getMaterial() {
        return material;
    }

    public String getCustomItem() {
        return customItem;
    }

    public String getAmount() {
        return amount;
    }

    public double getChance() {
        return chance;
    }
}

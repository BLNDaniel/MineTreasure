package com.danny.treasurechests;

import org.bukkit.Material;

public class LootItem {

    private final Material material;
    private final String amount;
    private final double chance;

    public LootItem(Material material, String amount, double chance) {
        this.material = material;
        this.amount = amount;
        this.chance = chance;
    }

    public Material getMaterial() {
        return material;
    }

    public String getAmount() {
        return amount;
    }

    public double getChance() {
        return chance;
    }
}

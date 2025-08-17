package com.danny.treasurechests;

import org.bukkit.Material;

public class LootItem {

    private final Material material;
    private final String customItem;
    private final String amount;
    private final double chance;
    private final java.util.List<String> potionEffects;

    public LootItem(org.bukkit.Material material, String customItem, String amount, double chance, java.util.List<String> potionEffects) {
        this.material = material;
        this.customItem = customItem;
        this.amount = amount;
        this.chance = chance;
        this.potionEffects = potionEffects;
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

    public java.util.List<String> getPotionEffects() {
        return potionEffects;
    }
}

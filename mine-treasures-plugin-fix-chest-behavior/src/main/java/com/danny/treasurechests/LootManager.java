package com.danny.treasurechests;

import com.danny.treasurechests.Animation.AnimationInfo;
import com.danny.treasurechests.Animation.ParticleEffect;
import com.danny.treasurechests.Animation.ScaleEffect;
import com.danny.treasurechests.Animation.SoundEffect;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LootManager {

    private final TreasureChests plugin;
    private final Map<String, LootTier> lootTiers = new HashMap<>();
    private double totalTierChance = 0.0;
    private final Random random = new Random();

    public LootManager(TreasureChests plugin) {
        this.plugin = plugin;
    }

    private AnimationInfo parseAnimationInfo(ConfigurationSection section) {
        if (section == null) {
            return new AnimationInfo(null, Collections.emptyList(), null);
        }

        // Parse Sound
        SoundEffect soundEffect = null;
        ConfigurationSection soundSection = section.getConfigurationSection("sound");
        if (soundSection != null) {
            String name = soundSection.getString("name");
            if (name != null && !name.isEmpty()) {
                soundEffect = new SoundEffect(
                        name,
                        (float) soundSection.getDouble("volume", 1.0),
                        (float) soundSection.getDouble("pitch", 1.0)
                );
            }
        }

        // Parse Particles
        List<ParticleEffect> particleEffects = new ArrayList<>();
        List<Map<?, ?>> particleMaps = section.getMapList("particles");
        for (Map<?, ?> particleMap : particleMaps) {
            try {
                String typeName = (String) particleMap.get("type");
                Particle type = Particle.valueOf(typeName.toUpperCase());
                int count = particleMap.get("count") != null ? (int) particleMap.get("count") : 10;
                double speed = particleMap.get("speed") != null ? (double) particleMap.get("speed") : 0.1;
                particleEffects.add(new ParticleEffect(type, count, speed));
            } catch (Exception e) {
                plugin.getLogger().warning("Fehler beim Verarbeiten eines Partikeleffekts in der Konfiguration: " + e.getMessage());
            }
        }

        // Parse Scale
        ScaleEffect scaleEffect = null;
        ConfigurationSection scaleSection = section.getConfigurationSection("scale");
        if (scaleSection != null) {
            scaleEffect = new ScaleEffect(
                scaleSection.getDouble("from", 1.0),
                scaleSection.getDouble("to", 1.0),
                scaleSection.getInt("duration", 20)
            );
        }

        return new AnimationInfo(soundEffect, particleEffects, scaleEffect);
    }


    public void loadLootTables() {
        plugin.getLogger().info("Lade Beutetabellen...");
        lootTiers.clear();
        totalTierChance = 0.0;

        ConfigurationSection tiersSection = plugin.getConfig().getConfigurationSection("loot-tables.tiers");
        if (tiersSection == null) {
            plugin.getLogger().warning("Sektion 'loot-tables.tiers' nicht in config.yml gefunden! Es wird keine Beute verfügbar sein.");
            return;
        }

        for (String tierName : tiersSection.getKeys(false)) {
            ConfigurationSection tierSection = tiersSection.getConfigurationSection(tierName);
            if (tierSection == null) continue;

            double tierChance = tierSection.getDouble("chance");
            String displayName = tierSection.getString("display-name", tierName);
            String headTexture = tierSection.getString("head-texture", "");

            String soundName = tierSection.getString("sound.name", "ENTITY_PLAYER_LEVELUP");
            boolean broadcastSound = tierSection.getBoolean("sound.broadcast", false);
            SoundInfo soundInfo = new SoundInfo(soundName, broadcastSound);

            AnimationInfo spawnAnimation = parseAnimationInfo(tierSection.getConfigurationSection("animations.spawn"));
            AnimationInfo despawnAnimation = parseAnimationInfo(tierSection.getConfigurationSection("animations.despawn"));


            List<LootItem> items = new ArrayList<>();
            List<Map<?, ?>> itemMaps = tierSection.getMapList("items");

            for (Map<?, ?> itemMap : itemMaps) {
                try {
                    String materialName = (String) itemMap.get("material");
                    org.bukkit.Material material = org.bukkit.Material.getMaterial(materialName.toUpperCase());
                    if (material == null) {
                        plugin.getLogger().warning("Ungültiges Material '" + materialName + "' in Stufe '" + tierName + "'. Gegenstand wird übersprungen.");
                        continue;
                    }

                    String amount = "1";
                    Object amountObj = itemMap.get("amount");
                    if (amountObj != null) {
                        amount = amountObj.toString();
                    }

                    double chance = 1.0;
                    Object chanceObj = itemMap.get("chance");
                    if (chanceObj instanceof Number) {
                        chance = ((Number) chanceObj).doubleValue();
                    }

                    items.add(new LootItem(material, amount, chance));
                } catch (Exception e) {
                    plugin.getLogger().severe("Fehler beim Verarbeiten eines Gegenstands in Stufe '" + tierName + "'. Fehler: " + e.getMessage());
                }
            }

            if (items.isEmpty()) {
                plugin.getLogger().warning("Keine gültigen Gegenstände für Stufe '" + tierName + "'. Diese Stufe wird übersprungen.");
                continue;
            }

            LootTier lootTier = new LootTier(tierName, displayName, headTexture, tierChance, soundInfo, items, spawnAnimation, despawnAnimation);
            lootTiers.put(tierName, lootTier);
            totalTierChance += tierChance;
            plugin.getLogger().info("Stufe '" + tierName + "' mit " + items.size() + " Gegenständen geladen.");
        }

        if (lootTiers.isEmpty()) {
            plugin.getLogger().severe("Keine gültigen Beutestufen geladen! Das Plugin wird nicht korrekt funktionieren.");
        } else {
            plugin.getLogger().info("Erfolgreich " + lootTiers.size() + " Beutestufen geladen.");
        }
    }

    public LootResult calculateRandomLoot() {
        LootTier chosenTier = chooseRandomTier();
        if (chosenTier == null) {
            return null;
        }

        List<ItemStack> items = new ArrayList<>();
        int rolls = parseAmount(plugin.getConfig().getString("loot-tables.rolls", "1"));

        for (int i = 0; i < rolls; i++) {
            LootItem chosenItem = chooseRandomItem(chosenTier);
            if (chosenItem != null) {
                items.add(createItemStack(chosenItem));
            }
        }

        if (items.isEmpty()) {
            return null; // If after all rolls no items were selected, return null
        }

        return new LootResult(chosenTier, items);
    }

    private LootTier chooseRandomTier() {
        if (lootTiers.isEmpty()) {
            return null;
        }

        double randomValue = random.nextDouble() * totalTierChance;
        double cumulativeWeight = 0.0;

        // Using an ordered list to have a guaranteed last element for the failsafe
        List<LootTier> tiers = new ArrayList<>(lootTiers.values());

        for (LootTier tier : tiers) {
            cumulativeWeight += tier.getChance();
            if (randomValue < cumulativeWeight) {
                return tier;
            }
        }

        // Failsafe for floating point inaccuracies or if chances don't sum up correctly.
        return tiers.get(tiers.size() - 1);
    }

    private LootItem chooseRandomItem(LootTier tier) {
        if (tier.getItems().isEmpty()) {
            return null;
        }

        // Shuffle items to avoid bias towards items listed first in the config
        List<LootItem> shuffledItems = new ArrayList<>(tier.getItems());
        Collections.shuffle(shuffledItems, random);

        for (LootItem item : shuffledItems) {
            if (random.nextDouble() < item.getChance()) {
                return item; // Return the first item that passes its chance roll
            }
        }

        return null; // If no item passes its chance roll, return null for this roll.
    }

    private ItemStack createItemStack(LootItem item) {
        return new ItemStack(item.getMaterial(), parseAmount(item.getAmount()));
    }

    private int parseAmount(String amountString) {
        if (amountString.contains("-")) {
            try {
                String[] parts = amountString.split("-");
                int min = Integer.parseInt(parts[0]);
                int max = Integer.parseInt(parts[1]);
                return random.nextInt((max - min) + 1) + min;
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        try {
            return Integer.parseInt(amountString);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public static class LootResult {
        private final LootTier tier;
        private final List<ItemStack> items;

        public LootResult(LootTier tier, List<ItemStack> items) {
            this.tier = tier;
            this.items = items;
        }

        public LootTier getTier() {
            return tier;
        }

        public List<ItemStack> getItems() {
            return items;
        }
    }
}

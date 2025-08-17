package com.danny.treasurechests;

import com.danny.treasurechests.Animation.AnimationInfo;
import com.danny.treasurechests.Animation.ParticleEffect;
import com.danny.treasurechests.Animation.ScaleEffect;
import org.bukkit.Bukkit;
import com.danny.treasurechests.Animation.SoundEffect;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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
    private volatile boolean lootTablesLoaded = false;

    public LootManager(TreasureChests plugin) {
        this.plugin = plugin;
    }

    public boolean areLootTablesLoaded() {
        return lootTablesLoaded;
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
                plugin.getLogger().warning(plugin.getMessageManager().getMessage("config-error", "%path%", "animations.particles", "%error%", e.getMessage()));
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

    public void loadLootTablesAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            loadLootTables();
            lootTablesLoaded = true;
        });
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
            int despawnTimer = tierSection.getInt("despawn-timer", 60);
            String displayName = tierSection.getString("display-name", tierName);
            String headTexture = tierSection.getString("head-texture", "");

            String soundName = tierSection.getString("sound.name", "ENTITY_PLAYER_LEVELUP");
            boolean broadcastSound = tierSection.getBoolean("sound.broadcast", false);
            SoundInfo soundInfo = new SoundInfo(soundName, broadcastSound);

            AnimationInfo spawnAnimation = parseAnimationInfo(tierSection.getConfigurationSection("animations.spawn"));
            AnimationInfo despawnAnimation = parseAnimationInfo(tierSection.getConfigurationSection("animations.despawn"));

            boolean broadcastEnabled = tierSection.getBoolean("broadcast-enabled", false);
            String broadcastMessage = tierSection.getString("broadcast-message", "&6&lSCHATZFUND! &e%player% &7hat eine &e%tier% &7Schatztruhe gefunden!");

            ConfigurationSection xpBoostSection = tierSection.getConfigurationSection("xp_boost");
            XpBoostInfo xpBoostInfo = null;
            if (xpBoostSection != null) {
                xpBoostInfo = new XpBoostInfo(
                        xpBoostSection.getBoolean("enabled"),
                        xpBoostSection.getDouble("modifier"),
                        xpBoostSection.getInt("duration")
                );
            }


            List<LootItem> items = new ArrayList<>();
            List<Map<?, ?>> itemMaps = tierSection.getMapList("items");

            for (Map<?, ?> itemMap : itemMaps) {
                try {
                    String materialName = (String) itemMap.get("material");
                    org.bukkit.Material material = org.bukkit.Material.getMaterial(materialName.toUpperCase());
                    if (material == null) {
                        plugin.getLogger().warning(plugin.getMessageManager().getMessage("invalid-material", "%material%", materialName, "%tier%", tierName));
                        continue;
                    }

                    String customItem = (String) itemMap.get("custom-item");
                    String itemDisplayName = (String) itemMap.get("display-name");
                    java.util.List<String> potionEffects = (java.util.List<String>) itemMap.get("potion-effects");
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

                    items.add(new LootItem(material, customItem, itemDisplayName, amount, chance, potionEffects));
                } catch (Exception e) {
                    plugin.getLogger().severe(plugin.getMessageManager().getMessage("config-error", "%path%", "items", "%error%", e.getMessage()));
                }
            }

            if (items.isEmpty()) {
                plugin.getLogger().warning(plugin.getMessageManager().getMessage("no-valid-items", "%tier%", tierName));
                continue;
            }

            LootTier lootTier = new LootTier(tierName, displayName, headTexture, tierChance, despawnTimer, soundInfo, items, spawnAnimation, despawnAnimation, broadcastEnabled, broadcastMessage, xpBoostInfo);
            lootTiers.put(tierName, lootTier);
            totalTierChance += tierChance;
            plugin.getLogger().info("Stufe '" + tierName + "' mit " + items.size() + " Gegenständen geladen.");
        }

        if (lootTiers.isEmpty()) {
            plugin.getLogger().severe(plugin.getMessageManager().getMessage("no-loot-tiers"));
        } else {
            plugin.getLogger().info("Erfolgreich " + lootTiers.size() + " Beutestufen geladen.");
        }
    }

    public LootResult calculateRandomLoot(Player player) {
        LootTier chosenTier = chooseRandomTier(player);
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

    public LootResult calculateLootForTier(String tierName) {
        LootTier chosenTier = lootTiers.get(tierName);
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

    private LootTier chooseRandomTier(Player player) {
        if (lootTiers.isEmpty()) {
            return null;
        }

        double luckMultiplier = plugin.getLuckBoosterManager().getLuckMultiplier(player);
        double adjustedTotalChance = 0;
        List<LootTier> tiers = new ArrayList<>(lootTiers.values());
        Map<LootTier, Double> adjustedChances = new HashMap<>();

        for (LootTier tier : tiers) {
            double adjustedChance = tier.getChance() * luckMultiplier;
            adjustedChances.put(tier, adjustedChance);
            adjustedTotalChance += adjustedChance;
        }

        double randomValue = random.nextDouble() * adjustedTotalChance;
        double cumulativeWeight = 0.0;

        for (LootTier tier : tiers) {
            cumulativeWeight += adjustedChances.get(tier);
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

        List<LootItem> successfulItems = new ArrayList<>();
        for (LootItem item : tier.getItems()) {
            if (random.nextDouble() < item.getChance()) {
                successfulItems.add(item);
            }
        }

        if (successfulItems.isEmpty()) {
            return null;
        }

        return successfulItems.get(random.nextInt(successfulItems.size()));
    }

    private ItemStack createItemStack(LootItem item) {
        if (item.getCustomItem() != null) {
            String customItem = item.getCustomItem();
            for (String tier : plugin.getConfig().getConfigurationSection("items.luck_boosters").getKeys(false)) {
                if (customItem.equalsIgnoreCase(tier) || customItem.equalsIgnoreCase(tier + "_luck_booster")) {
                    return plugin.getItemManager().createLuckBooster(tier);
                }
            }

            if (customItem.equalsIgnoreCase("golden_pickaxe")) {
                return plugin.getItemManager().createGoldenPickaxe();
            } else if (customItem.equalsIgnoreCase("infinity_water_bucket")) {
                return plugin.getItemManager().createInfinityWaterBucket();
            }
        }

        if (item.getMaterial() == org.bukkit.Material.POTION && item.getPotionEffects() != null) {
            ItemStack potion = new ItemStack(item.getMaterial(), parseAmount(item.getAmount()));
            org.bukkit.inventory.meta.PotionMeta meta = (org.bukkit.inventory.meta.PotionMeta) potion.getItemMeta();
            if (item.getDisplayName() != null) {
                meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', item.getDisplayName()));
            }
            for (String effect : item.getPotionEffects()) {
                String[] parts = effect.split(":");
                if (parts.length == 3) {
                    org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getByName(parts[0]);
                    int amplifier = Integer.parseInt(parts[1]) - 1;
                    int duration = Integer.parseInt(parts[2]) * 20; // in ticks
                    if (type != null) {
                        meta.addCustomEffect(new org.bukkit.potion.PotionEffect(type, duration, amplifier), true);
                    }
                }
            }
            potion.setItemMeta(meta);
            return potion;
        }

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

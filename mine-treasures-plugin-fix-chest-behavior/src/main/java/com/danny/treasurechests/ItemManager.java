package com.danny.treasurechests;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private final TreasureChests plugin;

    public ItemManager(TreasureChests plugin) {
        this.plugin = plugin;
    }

    public ItemStack createLuckBooster(String tier) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("items.luck_boosters." + tier);
        if (section == null) {
            return null;
        }

        ItemStack item = new ItemStack(Material.YELLOW_DYE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("display-name")));

        List<String> lore = new ArrayList<>();
        for (String line : section.getStringList("lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(lore);

        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.getPersistentDataContainer().set(plugin.getNamespacedKey("luck_booster_tier"), PersistentDataType.STRING, tier);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createGoldenPickaxe() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("items.golden_pickaxe");
        if (section == null) {
            return null;
        }

        ItemStack item = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("display-name")));

        List<String> lore = new ArrayList<>();
        for (String line : section.getStringList("lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(lore);

        for (String enchantment : section.getStringList("enchantments")) {
            String[] parts = enchantment.split(":");
            Enchantment ench = Enchantment.getByName(parts[0]);
            if (ench != null) {
                meta.addEnchant(ench, Integer.parseInt(parts[1]), true);
            }
        }

        meta.getPersistentDataContainer().set(plugin.getNamespacedKey("golden_pickaxe"), PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }
}

package com.danny.treasurechests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class LuckBoosterListener implements Listener {

    private final TreasureChests plugin;

    public LuckBoosterListener(TreasureChests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(plugin.getNamespacedKey("luck_booster_tier"), PersistentDataType.STRING)) {
            event.setCancelled(true);

            if (plugin.getLuckBoosterManager().hasBooster(player)) {
                player.sendMessage(plugin.getMessageManager().getMessage("luck-booster-already-active"));
                return;
            }

            String tier = meta.getPersistentDataContainer().get(plugin.getNamespacedKey("luck_booster_tier"), PersistentDataType.STRING);

            double multiplier = plugin.getConfig().getDouble("items.luck_boosters." + tier + ".multiplier");
            int duration = plugin.getConfig().getInt("items.luck_boosters." + tier + ".duration");

            plugin.getLuckBoosterManager().activateBooster(player, multiplier, duration);
            item.setAmount(item.getAmount() - 1);
        }
    }
}

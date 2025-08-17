package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardManager {

    private final TreasureChests plugin;
    private final Map<UUID, BossBar> xpBossBars = new HashMap<>();

    public RewardManager(TreasureChests plugin) {
        this.plugin = plugin;
    }

    public void applyRewards(Player player, Location location) {
        TreasureChestManager.TreasureChestData chestData = plugin.getTreasureChestManager().getChestDataAt(location);
        if (chestData == null) {
            return;
        }
        LootTier tier = chestData.tier();
        if (tier == null) {
            return;
        }

        XpBoostInfo xpBoostInfo = tier.getXpBoostInfo();
        if (xpBoostInfo != null && xpBoostInfo.enabled()) {
            applyXpBoost(player, xpBoostInfo.modifier(), xpBoostInfo.duration());
        }
    }

    private void applyXpBoost(Player player, double modifier, int duration) {
        if (xpBossBars.containsKey(player.getUniqueId())) {
            return;
        }

        String modifierString;
        if (modifier == (int) modifier) {
            modifierString = String.valueOf((int) modifier);
        } else {
            modifierString = String.valueOf(modifier);
        }
        String command = "lp user " + player.getName() + " permission settemp auraskills.multiplier." + modifierString + " true " + duration + "s";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        BossBar bossBar = Bukkit.createBossBar(
                plugin.getMessageManager().getPlainMessage("xp-boost-bossbar", "%time%", formatTime(duration)),
                BarColor.PURPLE,
                BarStyle.SOLID
        );
        bossBar.addPlayer(player);
        xpBossBars.put(player.getUniqueId(), bossBar);

        final int[] timeLeft = {duration};
        new BukkitRunnable() {
            @Override
            public void run() {
                timeLeft[0]--;
                if (timeLeft[0] < 0) {
                    BossBar bb = xpBossBars.remove(player.getUniqueId());
                    if (bb != null) {
                        bb.removePlayer(player);
                    }
                    cancel();
                    return;
                }
                bossBar.setProgress((double) timeLeft[0] / duration);
                bossBar.setTitle(plugin.getMessageManager().getPlainMessage("xp-boost-bossbar", "%time%", formatTime(timeLeft[0])));
            }
        }.runTaskTimer(plugin, 20, 20);

        player.sendMessage(plugin.getMessageManager().getMessage("xp-boost-activated", "%time%", String.valueOf(duration / 60)));
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}

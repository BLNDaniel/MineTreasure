package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LuckBoosterManager {

    private final TreasureChests plugin;
    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private final Map<UUID, Double> luckBoosters = new HashMap<>();
    private final Map<UUID, Integer> remainingTime = new HashMap<>();

    public LuckBoosterManager(TreasureChests plugin) {
        this.plugin = plugin;
    }

    public void activateBooster(Player player, double multiplier, int duration) {
        luckBoosters.put(player.getUniqueId(), multiplier);
        remainingTime.put(player.getUniqueId(), duration);

        BossBar bossBar = Bukkit.createBossBar(
                plugin.getMessageManager().getPlainMessage("luck-booster-bossbar", "%time%", formatTime(duration)),
                BarColor.PURPLE,
                BarStyle.SOLID
        );
        bossBar.addPlayer(player);
        bossBars.put(player.getUniqueId(), bossBar);

        new BukkitRunnable() {
            @Override
            public void run() {
                int time = remainingTime.get(player.getUniqueId()) - 1;
                if (time < 0) {
                    deactivateBooster(player);
                    cancel();
                    return;
                }
                remainingTime.put(player.getUniqueId(), time);
                bossBar.setProgress((double) time / duration);
                bossBar.setTitle(plugin.getMessageManager().getPlainMessage("luck-booster-bossbar", "%time%", formatTime(time)));
            }
        }.runTaskTimer(plugin, 20, 20);

        player.sendMessage(plugin.getMessageManager().getMessage("luck-booster-activated", "%time%", String.valueOf(duration / 60)));
    }

    public void deactivateBooster(Player player) {
        luckBoosters.remove(player.getUniqueId());
        remainingTime.remove(player.getUniqueId());
        BossBar bossBar = bossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    public double getLuckMultiplier(Player player) {
        return luckBoosters.getOrDefault(player.getUniqueId(), 1.0);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}

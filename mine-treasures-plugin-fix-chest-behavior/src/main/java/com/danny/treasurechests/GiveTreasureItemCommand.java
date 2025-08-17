package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveTreasureItemCommand implements CommandExecutor {

    private final TreasureChests plugin;

    public GiveTreasureItemCommand(TreasureChests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessageManager().getMessage("unknown-command"));
            return false;
        }

        String itemName = args[0];
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("player-not-found"));
            return false;
        }

        ItemStack item = null;
        if (plugin.getConfig().getConfigurationSection("items.luck_boosters").getKeys(false).contains(itemName)) {
            item = plugin.getItemManager().createLuckBooster(itemName);
        } else if (itemName.equalsIgnoreCase("golden_pickaxe")) {
            item = plugin.getItemManager().createGoldenPickaxe();
        } else if (itemName.equalsIgnoreCase("infinity_water_bucket")) {
            item = plugin.getItemManager().createInfinityWaterBucket();
        }

        if (item == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("item-not-found"));
            return false;
        }

        target.getInventory().addItem(item);
        sender.sendMessage(plugin.getMessageManager().getMessage("item-given", "%player%", target.getName()));
        return true;
    }
}

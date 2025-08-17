package com.danny.treasurechests;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final JavaPlugin plugin;
    private FileConfiguration messagesConfig;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Load default messages from the JAR
        InputStream defaultConfigStream = plugin.getResource("messages.yml");
        if (defaultConfigStream != null) {
            messagesConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream, StandardCharsets.UTF_8)));
        }
    }

    public String getMessage(String path, String... replacements) {
        String prefix = messagesConfig.getString("prefix", "");
        String message = messagesConfig.getString(path, path);
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', prefix + " " + message);
    }

    public String getPlainMessage(String path, String... replacements) {
        String message = messagesConfig.getString(path, path);
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
    }
}

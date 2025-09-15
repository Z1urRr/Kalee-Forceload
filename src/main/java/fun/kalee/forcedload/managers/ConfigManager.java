package fun.kalee.forcedload.managers;

import fun.kalee.forcedload.KaleeForceload;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final KaleeForceload plugin;
    private FileConfiguration config;

    public ConfigManager(KaleeForceload plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public double getChunkPrice() {
        return config.getDouble("economy.chunk-price", 1000.0);
    }

    public double getChunkPrice(String timeKey) {
        return config.getDouble("economy.chunk-prices." + timeKey, 1000.0);
    }

    public String getCurrencyName() {
        return config.getString("economy.currency-name", "金币");
    }

    public int getMaxChunksPerPlayer() {
        return config.getInt("chunk.max-chunks-per-player", 10);
    }

    public int getMaxChunksForPlayer(org.bukkit.entity.Player player) {
        if (player.hasPermission("kalee.forceload.vip")) {
            return config.getInt("chunk.max-chunks-by-permission.kalee.forceload.vip", 15);
        } else if (player.hasPermission("kalee.forceload.default")) {
            return config.getInt("chunk.max-chunks-by-permission.kalee.forceload.default", 5);
        } else {
            return config.getInt("chunk.default-max-chunks", 3);
        }
    }

    public int getExpireHours() {
        return config.getInt("chunk.expire-hours", 0);
    }

    public String getMessage(String key) {
        String message = config.getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }
        return message;
    }

    public String getPrefix() {
        return getMessage("prefix");
    }

    public String getGuiTitle(String guiType) {
        return ChatColor.translateAlternateColorCodes('&',
            config.getString("gui." + guiType + ".title", "&6GUI"));
    }

    public int getGuiSize(String guiType) {
        return config.getInt("gui." + guiType + ".size", 27);
    }
}


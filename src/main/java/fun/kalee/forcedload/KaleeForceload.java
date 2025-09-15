package fun.kalee.forcedload;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;
import fun.kalee.forcedload.commands.KaleeForceloadCommand;
import fun.kalee.forcedload.managers.ChunkManager;
import fun.kalee.forcedload.managers.ConfigManager;
import fun.kalee.forcedload.listeners.MenuListener;

public class KaleeForceload extends JavaPlugin {
    private static KaleeForceload instance;
    private Economy economy = null;
    private ChunkManager chunkManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        if (!setupEconomy()) {
            getLogger().severe("未找到Vault插件或经济系统插件！插件将被禁用！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        chunkManager = new ChunkManager(this);
        getCommand("kaleeforceload").setExecutor(new KaleeForceloadCommand(this));
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getLogger().info("KaleeForceload插件已成功启用！");
    }

    @Override
    public void onDisable() {
        if (chunkManager != null) {
            chunkManager.saveData();
        }
        getLogger().info("KaleeForceload插件已禁用！");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static KaleeForceload getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}


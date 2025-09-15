package fun.kalee.forcedload.managers;

import fun.kalee.forcedload.KaleeForceload;
import fun.kalee.forcedload.data.ChunkData;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkManager {
    private final KaleeForceload plugin;
    private final Map<String, ChunkData> loadedChunks;
    private final Map<UUID, Set<ChunkData>> playerChunks;
    private File dataFile;
    private FileConfiguration dataConfig;

    public ChunkManager(KaleeForceload plugin) {
        this.plugin = plugin;
        this.loadedChunks = new ConcurrentHashMap<>();
        this.playerChunks = new ConcurrentHashMap<>();
        initializeDataFile();
        loadData();
        startChunkLoader();
    }

    private void initializeDataFile() {
        dataFile = new File(plugin.getDataFolder(), "chunks.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建数据文件: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void loadData() {
        loadedChunks.clear();
        playerChunks.clear();
        if (!dataConfig.contains("chunks")) {
            return;
        }
        for (String chunkKey : dataConfig.getConfigurationSection("chunks").getKeys(false)) {
            try {
                String path = "chunks." + chunkKey;
                UUID ownerId = UUID.fromString(dataConfig.getString(path + ".owner"));
                String worldName = dataConfig.getString(path + ".world");
                int chunkX = dataConfig.getInt(path + ".x");
                int chunkZ = dataConfig.getInt(path + ".z");
                long purchaseTime = dataConfig.getLong(path + ".purchase-time");
                long expireTime = dataConfig.getLong(path + ".expire-time", 0);
                ChunkData chunkData = new ChunkData(ownerId, worldName, chunkX, chunkZ, purchaseTime, expireTime);
                if (!chunkData.isExpired()) {
                    loadedChunks.put(chunkKey, chunkData);
                    playerChunks.computeIfAbsent(ownerId, k -> new HashSet<>()).add(chunkData);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("加载区块数据时出错 " + chunkKey + " - " + e.getMessage());
            }
        }
        plugin.getLogger().info("已加载 " + loadedChunks.size() + " 个区块数据");
    }

    public void saveData() {
        dataConfig.set("chunks", null);
        for (Map.Entry<String, ChunkData> entry : loadedChunks.entrySet()) {
            String chunkKey = entry.getKey();
            ChunkData chunkData = entry.getValue();
            String path = "chunks." + chunkKey;
            dataConfig.set(path + ".owner", chunkData.getOwnerId().toString());
            dataConfig.set(path + ".world", chunkData.getWorldName());
            dataConfig.set(path + ".x", chunkData.getChunkX());
            dataConfig.set(path + ".z", chunkData.getChunkZ());
            dataConfig.set(path + ".purchase-time", chunkData.getPurchaseTime());
            dataConfig.set(path + ".expire-time", chunkData.getExpireTime());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("保存数据文件时出错 " + e.getMessage());
        }
    }

    public void purchaseChunk(Player player, Chunk chunk) {
        long currentTime = System.currentTimeMillis();
        long expireTime = 0;
        int expireHours = plugin.getConfigManager().getExpireHours();
        if (expireHours > 0) {
            expireTime = currentTime + (expireHours * 60 * 60 * 1000L);
        }
        ChunkData chunkData = new ChunkData(
            player.getUniqueId(),
            chunk.getWorld().getName(),
            chunk.getX(),
            chunk.getZ(),
            currentTime,
            expireTime
        );
        String chunkKey = chunkData.getChunkKey();
        loadedChunks.put(chunkKey, chunkData);
        playerChunks.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(chunkData);
        chunk.setForceLoaded(true);
        saveData();
    }

    public void purchaseChunk(Player player, Chunk chunk, int days) {
        long currentTime = System.currentTimeMillis();
        long expireTime = currentTime + (days * 24 * 60 * 60 * 1000L);
        ChunkData chunkData = new ChunkData(
            player.getUniqueId(),
            chunk.getWorld().getName(),
            chunk.getX(),
            chunk.getZ(),
            currentTime,
            expireTime
        );
        String chunkKey = chunkData.getChunkKey();
        loadedChunks.put(chunkKey, chunkData);
        playerChunks.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(chunkData);
        chunk.setForceLoaded(true);
        saveData();
    }

    public boolean isChunkOwned(Chunk chunk) {
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        return loadedChunks.containsKey(chunkKey);
    }

    public ChunkData getChunkData(Chunk chunk) {
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        return loadedChunks.get(chunkKey);
    }

    public Set<ChunkData> getPlayerChunks(UUID playerId) {
        return playerChunks.getOrDefault(playerId, new HashSet<>());
    }

    public int getPlayerChunkCount(UUID playerId) {
        return getPlayerChunks(playerId).size();
    }

    public void removeChunk(ChunkData chunkData) {
        String chunkKey = chunkData.getChunkKey();
        loadedChunks.remove(chunkKey);
        Set<ChunkData> playerChunkSet = playerChunks.get(chunkData.getOwnerId());
        if (playerChunkSet != null) {
            playerChunkSet.remove(chunkData);
            if (playerChunkSet.isEmpty()) {
                playerChunks.remove(chunkData.getOwnerId());
            }
        }
        World world = Bukkit.getWorld(chunkData.getWorldName());
        if (world != null) {
            Chunk chunk = world.getChunkAt(chunkData.getChunkX(), chunkData.getChunkZ());
            chunk.setForceLoaded(false);
        }
        saveData();
    }

    private void startChunkLoader() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (ChunkData chunkData : loadedChunks.values()) {
                World world = Bukkit.getWorld(chunkData.getWorldName());
                if (world != null) {
                    Chunk chunk = world.getChunkAt(chunkData.getChunkX(), chunkData.getChunkZ());
                    chunk.setForceLoaded(true);
                }
            }
            plugin.getLogger().info("已强制加载 " + loadedChunks.size() + " 个区块");
        }, 20L);
        if (plugin.getConfigManager().getExpireHours() > 0) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkExpiredChunks, 20L * 60 * 5, 20L * 60 * 5);
        }
    }

    private void checkExpiredChunks() {
        List<ChunkData> expiredChunks = new ArrayList<>();
        for (ChunkData chunkData : loadedChunks.values()) {
            if (chunkData.isExpired()) {
                expiredChunks.add(chunkData);
            }
        }
        if (!expiredChunks.isEmpty()) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (ChunkData chunkData : expiredChunks) {
                    removeChunk(chunkData);
                    plugin.getLogger().info("区块 " + chunkData.getChunkKey() + " 已过期并被移除");
                }
            });
        }
    }

    public Map<String, ChunkData> getAllLoadedChunks() {
        return new HashMap<>(loadedChunks);
    }
}


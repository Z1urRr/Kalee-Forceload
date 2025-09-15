package fun.kalee.forcedload.listeners;

import fun.kalee.forcedload.KaleeForceload;
import fun.kalee.forcedload.gui.ChunkListGUI;
import fun.kalee.forcedload.gui.MainMenuGUI;
import fun.kalee.forcedload.gui.TimeSelectGUI;
import fun.kalee.forcedload.data.ChunkData;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {
    private final KaleeForceload plugin;

    public MenuListener(KaleeForceload plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        String mainMenuTitle = plugin.getConfigManager().getGuiTitle("main-menu");
        String chunkListTitle = plugin.getConfigManager().getGuiTitle("chunk-list");
        String timeSelectTitle = plugin.getConfigManager().getGuiTitle("time-select");
        if (!title.equals(mainMenuTitle) && !title.equals(chunkListTitle) && !title.equals(timeSelectTitle)) {
            return;
        }
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        if (title.equals(mainMenuTitle)) {
            handleMainMenuClick(player, clickedItem, event.getSlot());
        } else if (title.equals(chunkListTitle)) {
            handleChunkListClick(player, clickedItem, event.getSlot(), event.getClick());
        } else if (title.equals(timeSelectTitle)) {
            handleTimeSelectClick(player, clickedItem, event.getSlot());
        }
    }

    private void handleMainMenuClick(Player player, ItemStack item, int slot) {
        switch (slot) {
            case 11:
                if (item.getType() == Material.EMERALD) {
                    player.closeInventory();
                    TimeSelectGUI timeSelectGUI = new TimeSelectGUI(plugin);
                    timeSelectGUI.openMenu(player);
                }
                break;
            case 15:
                if (item.getType() == Material.MAP) {
                    player.closeInventory();
                    ChunkListGUI chunkListGUI = new ChunkListGUI(plugin);
                    chunkListGUI.openMenu(player);
                }
                break;
        }
    }

    private void handleChunkListClick(Player player, ItemStack item, int slot, ClickType clickType) {
        if (item.getType() == Material.ARROW) {
            player.closeInventory();
            MainMenuGUI mainMenuGUI = new MainMenuGUI(plugin);
            mainMenuGUI.openMenu(player);
            return;
        }
        if (item.getType() == Material.LIME_STAINED_GLASS_PANE && clickType == ClickType.RIGHT) {
            handleChunkDeletion(player, item, slot);
        }
    }

    private void handleTimeSelectClick(Player player, ItemStack item, int slot) {
        if (item.getType() == Material.ARROW) {
            player.closeInventory();
            MainMenuGUI mainMenuGUI = new MainMenuGUI(plugin);
            mainMenuGUI.openMenu(player);
            return;
        }
        String timeKey = null;
        int days = 0;
        switch (slot) {
            case 10:
                if (item.getType() == Material.LIME_DYE) {
                    timeKey = "1-day";
                    days = 1;
                }
                break;
            case 11:
                if (item.getType() == Material.YELLOW_DYE) {
                    timeKey = "3-day";
                    days = 3;
                }
                break;
            case 12:
                if (item.getType() == Material.ORANGE_DYE) {
                    timeKey = "5-day";
                    days = 5;
                }
                break;
            case 14:
                if (item.getType() == Material.RED_DYE) {
                    timeKey = "7-day";
                    days = 7;
                }
                break;
            case 15:
                if (item.getType() == Material.PURPLE_DYE) {
                    timeKey = "15-day";
                    days = 15;
                }
                break;
        }
        if (timeKey != null && days > 0) {
            handleTimedChunkPurchase(player, timeKey, days);
        }
    }

    private void handleChunkDeletion(Player player, ItemStack item, int slot) {
        if (item.getItemMeta() != null && item.getItemMeta().getLore() != null && !item.getItemMeta().getLore().isEmpty()) {
            String lore = item.getItemMeta().getLore().get(0);
            if (lore.contains("区块位置:")) {
                try {
                    String coords = lore.replaceAll(".*区块位置: §e", "");
                    coords = coords.replace("§", "");
                    String[] parts = coords.split(", ");
                    int chunkX = Integer.parseInt(parts[0]);
                    int chunkZ = Integer.parseInt(parts[1]);
                    for (ChunkData chunkData : plugin.getChunkManager().getPlayerChunks(player.getUniqueId())) {
                        if (chunkData.getChunkX() == chunkX && chunkData.getChunkZ() == chunkZ) {
                            plugin.getChunkManager().removeChunk(chunkData);
                            player.sendMessage(plugin.getConfigManager().getPrefix() + "§c已删除区块 " + chunkX + ", " + chunkZ + "（不退款）");
                            player.closeInventory();
                            ChunkListGUI chunkListGUI = new ChunkListGUI(plugin);
                            chunkListGUI.openMenu(player);
                            return;
                        }
                    }
                } catch (Exception e) {
                    player.sendMessage(plugin.getConfigManager().getPrefix() + "§c删除区块时出错！");
                }
            }
        }
    }

    private void handleTimedChunkPurchase(Player player, String timeKey, int days) {
        player.closeInventory();
        if (plugin.getEconomy() == null) {
            player.sendMessage(plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("economy-not-found"));
            return;
        }
        Chunk chunk = player.getLocation().getChunk();
        if (plugin.getChunkManager().isChunkOwned(chunk)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("chunk-already-owned"));
            return;
        }
        int maxChunks = plugin.getConfigManager().getMaxChunksPerPlayer();
        int currentChunks = plugin.getChunkManager().getPlayerChunkCount(player.getUniqueId());
        if (currentChunks >= maxChunks && !player.hasPermission("kaleeforceload.bypass")) {
            player.sendMessage(plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("max-chunks-reached", "max", String.valueOf(maxChunks)));
            return;
        }
        double price = plugin.getConfigManager().getChunkPrice(timeKey);
        double balance = plugin.getEconomy().getBalance(player);
        String currencyName = plugin.getConfigManager().getCurrencyName();
        if (balance < price) {
            player.sendMessage(plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("insufficient-funds",
                    "price", String.format("%.2f", price),
                    "currency", currencyName,
                    "balance", String.format("%.2f", balance)));
            return;
        }
        if (!plugin.getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§c交易失败，请稍后重试！");
            return;
        }
        plugin.getChunkManager().purchaseChunk(player, chunk, days);
        player.sendMessage(plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("chunk-purchased",
                "x", String.valueOf(chunk.getX()),
                "z", String.valueOf(chunk.getZ()),
                "price", String.format("%.2f", price),
                "currency", currencyName) + " §7(" + days + "天)");
    }

    private void handleChunkPurchase(Player player) {
        player.closeInventory();
        if (plugin.getEconomy() == null) {
            player.sendMessage(plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("economy-not-found"));
            return;
        }
        Chunk chunk = player.getLocation().getChunk();
        if (plugin.getChunkManager().isChunkOwned(chunk)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("chunk-already-owned"));
            return;
        }
        int maxChunks = plugin.getConfigManager().getMaxChunksPerPlayer();
        int currentChunks = plugin.getChunkManager().getPlayerChunkCount(player.getUniqueId());
        if (currentChunks >= maxChunks && !player.hasPermission("kaleeforceload.bypass")) {
            player.sendMessage(plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("max-chunks-reached", "max", String.valueOf(maxChunks)));
            return;
        }
        double price = plugin.getConfigManager().getChunkPrice();
        double balance = plugin.getEconomy().getBalance(player);
        String currencyName = plugin.getConfigManager().getCurrencyName();
        if (balance < price) {
            player.sendMessage(plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("insufficient-funds",
                    "price", String.format("%.2f", price),
                    "currency", currencyName,
                    "balance", String.format("%.2f", balance)));
            return;
        }
        if (!plugin.getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§c交易失败，请稍后重试！");
            return;
        }
        plugin.getChunkManager().purchaseChunk(player, chunk);
        player.sendMessage(plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("chunk-purchased",
                "x", String.valueOf(chunk.getX()),
                "z", String.valueOf(chunk.getZ()),
                "price", String.format("%.2f", price),
                "currency", currencyName));
    }
}


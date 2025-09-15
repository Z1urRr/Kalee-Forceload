package fun.kalee.forcedload.gui;

import fun.kalee.forcedload.KaleeForceload;
import fun.kalee.forcedload.data.ChunkData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChunkListGUI {
    private final KaleeForceload plugin;

    public ChunkListGUI(KaleeForceload plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        String title = plugin.getConfigManager().getGuiTitle("chunk-list");
        int size = plugin.getConfigManager().getGuiSize("chunk-list");
        Inventory inventory = Bukkit.createInventory(null, size, title);

        Set<ChunkData> playerChunks = plugin.getChunkManager().getPlayerChunks(player.getUniqueId());
        if (playerChunks.isEmpty()) {
            ItemStack noChunksItem = new ItemStack(Material.BARRIER);
            ItemMeta noChunksMeta = noChunksItem.getItemMeta();
            noChunksMeta.setDisplayName("§c没有已购买的区块");
            noChunksMeta.setLore(List.of(
                "§7你还没有购买任何区块",
                "§7使用主菜单购买区块"
            ));
            noChunksItem.setItemMeta(noChunksMeta);
            inventory.setItem(22, noChunksItem);
        } else {
            int slot = 0;
            for (ChunkData chunkData : playerChunks) {
                if (slot >= size - 9) break;
                ItemStack chunkItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta chunkMeta = chunkItem.getItemMeta();
                chunkMeta.setDisplayName("§a已购买区块");
                List<String> lore = new ArrayList<>();
                lore.add("§7区块位置: §e" + chunkData.getChunkX() + ", " + chunkData.getChunkZ());
                lore.add("§7世界: §e" + chunkData.getWorldName());
                lore.add("§7购买时间: §e" + chunkData.getPurchaseTimeFormatted());
                lore.add("§7到期时间: §e" + chunkData.getExpireTimeFormatted());
                lore.add("");
                lore.add("§7状态 §a强制加载中");
                lore.add("");
                lore.add("§c右键点击删除区块（不退款）");
                chunkMeta.setLore(lore);
                chunkItem.setItemMeta(chunkMeta);
                inventory.setItem(slot, chunkItem);
                slot++;
            }
        }

        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("§e返回主菜单");
        backItem.setItemMeta(backMeta);
        inventory.setItem(size - 5, backItem);

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassMeta);
        for (int i = size - 9; i < size; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, glassPane);
            }
        }
        player.openInventory(inventory);
    }
}


package fun.kalee.forcedload.gui;

import fun.kalee.forcedload.KaleeForceload;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MainMenuGUI {
    private final KaleeForceload plugin;

    public MainMenuGUI(KaleeForceload plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        String title = plugin.getConfigManager().getGuiTitle("main-menu");
        int size = plugin.getConfigManager().getGuiSize("main-menu");
        Inventory inventory = Bukkit.createInventory(null, size, title);

        ItemStack buyChunkItem = new ItemStack(Material.EMERALD);
        ItemMeta buyChunkMeta = buyChunkItem.getItemMeta();
        buyChunkMeta.setDisplayName("§a购买区块");
        buyChunkMeta.setLore(Arrays.asList(
            "§7点击打开购买列表",
            "§7购买后该区块将保持加载状态",
            "",
            "§e点击购买"
        ));
        buyChunkItem.setItemMeta(buyChunkMeta);
        inventory.setItem(11, buyChunkItem);

        ItemStack viewChunksItem = new ItemStack(Material.MAP);
        ItemMeta viewChunksMeta = viewChunksItem.getItemMeta();
        viewChunksMeta.setDisplayName("§b查看已购买的区块");
        viewChunksMeta.setLore(Arrays.asList(
            "§7查看你已经购买的所有区块",
            "§7管理你的强制加载区块",
            "",
            "§e点击查看"
        ));
        viewChunksItem.setItemMeta(viewChunksMeta);
        inventory.setItem(15, viewChunksItem);

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassMeta);

        for (int i = 0; i < size; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, glassPane);
            }
        }

        player.openInventory(inventory);
    }
}


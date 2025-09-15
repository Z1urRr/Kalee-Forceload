package fun.kalee.forcedload.gui;

import fun.kalee.forcedload.KaleeForceload;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TimeSelectGUI {
    private final KaleeForceload plugin;

    public TimeSelectGUI(KaleeForceload plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        String title = plugin.getConfigManager().getGuiTitle("time-select");
        int size = plugin.getConfigManager().getGuiSize("time-select");
        Inventory inventory = Bukkit.createInventory(null, size, title);
        String currencyName = plugin.getConfigManager().getCurrencyName();

        ItemStack day1Item = new ItemStack(Material.LIME_DYE);
        ItemMeta day1Meta = day1Item.getItemMeta();
        day1Meta.setDisplayName("§a1天");
        day1Meta.setLore(Arrays.asList(
            "§7购买1天的区块强制加载",
            "§7价格: §e" + plugin.getConfigManager().getChunkPrice("1-day") + " " + currencyName,
            "",
            "§e点击购买"
        ));
        day1Item.setItemMeta(day1Meta);
        inventory.setItem(10, day1Item);

        ItemStack day3Item = new ItemStack(Material.YELLOW_DYE);
        ItemMeta day3Meta = day3Item.getItemMeta();
        day3Meta.setDisplayName("§e3天");
        day3Meta.setLore(Arrays.asList(
            "§7购买3天的区块强制加载",
            "§7价格: §e" + plugin.getConfigManager().getChunkPrice("3-day") + " " + currencyName,
            "",
            "§e点击购买"
        ));
        day3Item.setItemMeta(day3Meta);
        inventory.setItem(11, day3Item);

        ItemStack day5Item = new ItemStack(Material.ORANGE_DYE);
        ItemMeta day5Meta = day5Item.getItemMeta();
        day5Meta.setDisplayName("§65天");
        day5Meta.setLore(Arrays.asList(
            "§7购买5天的区块强制加载",
            "§7价格: §e" + plugin.getConfigManager().getChunkPrice("5-day") + " " + currencyName,
            "",
            "§e点击购买"
        ));
        day5Item.setItemMeta(day5Meta);
        inventory.setItem(12, day5Item);

        ItemStack day7Item = new ItemStack(Material.RED_DYE);
        ItemMeta day7Meta = day7Item.getItemMeta();
        day7Meta.setDisplayName("§c7天");
        day7Meta.setLore(Arrays.asList(
            "§7购买7天的区块强制加载",
            "§7价格: §e" + plugin.getConfigManager().getChunkPrice("7-day") + " " + currencyName,
            "",
            "§e点击购买"
        ));
        day7Item.setItemMeta(day7Meta);
        inventory.setItem(14, day7Item);

        ItemStack day15Item = new ItemStack(Material.PURPLE_DYE);
        ItemMeta day15Meta = day15Item.getItemMeta();
        day15Meta.setDisplayName("§515天");
        day15Meta.setLore(Arrays.asList(
            "§7购买15天的区块强制加载",
            "§7价格: §e" + plugin.getConfigManager().getChunkPrice("15-day") + " " + currencyName,
            "",
            "§e点击购买"
        ));
        day15Item.setItemMeta(day15Meta);
        inventory.setItem(15, day15Item);

        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("§e返回主菜单");
        backItem.setItemMeta(backMeta);
        inventory.setItem(22, backItem);

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


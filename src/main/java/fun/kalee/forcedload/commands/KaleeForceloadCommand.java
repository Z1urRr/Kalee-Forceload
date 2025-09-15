package fun.kalee.forcedload.commands;

import fun.kalee.forcedload.KaleeForceload;
import fun.kalee.forcedload.gui.MainMenuGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KaleeForceloadCommand implements CommandExecutor {
    
    private final KaleeForceload plugin;
    
    public KaleeForceloadCommand(KaleeForceload plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c此指令只能由玩家执行！");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("kaleeforceload.use")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "menu":
                openMainMenu(player);
                break;
            case "checkmoney":
                checkMoney(player);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void openMainMenu(Player player) {
        MainMenuGUI mainMenu = new MainMenuGUI(plugin);
        mainMenu.openMenu(player);
    }
    
    private void checkMoney(Player player) {
        if (plugin.getEconomy() == null) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("economy-not-found"));
            return;
        }
        
        double balance = plugin.getEconomy().getBalance(player);
        String currencyName = plugin.getConfigManager().getCurrencyName();
        
        player.sendMessage(plugin.getConfigManager().getPrefix() + 
            plugin.getConfigManager().getMessage("balance-info", 
                "balance", String.format("%.2f", balance),
                "currency", currencyName));
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== KaleeForceload 帮助 ===");
        player.sendMessage("§e/kaleeforceload menu §7- 打开主菜单");
        player.sendMessage("§e/kaleeforceload checkmoney §7- 查看余额");
    }
}

package me.mangokevin.oreTycoon.commands.tycooncmds;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.menuManager.OverviewMenu;
import me.mangokevin.oreTycoon.menuManager.StatsMenu;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.tycoonManagment.booster.AutoMinerSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SellMultiplyBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SpawnSpeedBooster;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TycoonCmd implements CommandExecutor {

    private final OreTycoon plugin;
    private final TycoonBlockManager blockManager;
    private final MenuManager menuManager;

    public TycoonCmd(OreTycoon plugin, TycoonBlockManager blockManager) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.menuManager = plugin.getMenuManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player p)) {
            return true;
        }

        if (args.length == 0) {
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dm open tycoon_menu " + p.getName());
            new OverviewMenu(plugin, 0).open(p);
            return true;
        }
        String action = args[0].toLowerCase();

        switch (action) {
            case "booster":
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Usage: /tycoon " + action + " <booster_type>");
                    return true;
                }
                String boosterType = args[1];
                if (boosterType == null) {
                    return true;
                }
                switch (boosterType) {
                    case "sellmultiplier":
                        p.getInventory().addItem(new SellMultiplyBooster(0.3, 20L * 60 * 2).getItem());
                        break;
                    case "autominer":
                        p.getInventory().addItem(new AutoMinerSpeedBooster(20D, 20L * 60 * 2).getItem());
                        break;
                    case "spawnspeed":
                        p.getInventory().addItem(new SpawnSpeedBooster(20D, 20L * 60 * 2).getItem());
                        break;
                    case "all":
                        p.getInventory().addItem(new SellMultiplyBooster(0.3, 20L * 60 * 2).getItem());
                        p.getInventory().addItem(new AutoMinerSpeedBooster(20D, 20L * 60 * 2).getItem());
                        p.getInventory().addItem(new SpawnSpeedBooster(20D, 20L * 60 * 2).getItem());
                    default:
                        return true;
                }


                break;
            case "runtest":
                if (args.length == 4) {
                    try {
                        // Umwandeln der Strings in die benötigten Formate
                        int level = Integer.parseInt(args[1]);
                        double base = Double.parseDouble(args[2]);
                        double multi = Double.parseDouble(args[3]);

                        // Aufruf deiner Test-Methode
                        TycoonUpgrades.testUpgradeCostFunction(level, base, multi);

                        p.sendMessage("§aRunning test, view Console!");
                    } catch (NumberFormatException e) {
                        // Wird ausgelöst, wenn der Spieler keine gültigen Zahlen eingibt
                        p.sendMessage("§cERROR");
                        p.sendMessage("§7Usage: /command runtest <level, int> <base, double> <multi, double>");
                    }
                }

                break;
            case "toggle_selected":
                handleToggle(p);
                break;
            case "toggle_all":
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon toggle_all <on/off>");
                    return true;
                }
                List<TycoonBlock> tycoonBlockList = blockManager.getTycoonBlocksFromPlayer(p.getUniqueId());
                String state;
                if (args[1] == null) {
                    p.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon toggle_all <on/off>");
                    return true;
                }
                state = args[1];
                switch (state) {
                    case "on":
                        for (TycoonBlock tycoonBlock : tycoonBlockList) {
                            tycoonBlock.setActive(true);
                        }
                        return true;
                    case "off":
                        for (TycoonBlock tycoonBlock : tycoonBlockList) {
                            tycoonBlock.setActive(false);
                        }
                        return true;
                    default:
                        p.sendMessage(ChatColor.RED + "Incorrect Usage. Use /tycoon toggle_all <on/off>");
                        return true;
                }
            case "give", "block":
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Usage: /tycoon " + action + " <type>");
                    return true;
                }
                String type = args[1];
                if (type == null) {
                    return true;
                }
                switch (type) {
                    case "wood":
                        blockManager.giveTycoonBlock(p, TycoonType.WOOD);
                        return true;
                    case "stone":
                        blockManager.giveTycoonBlock(p, TycoonType.STONE);
                        return true;
                    case "coal":
                        blockManager.giveTycoonBlock(p, TycoonType.COAL);
                        return true;
                    case "iron":
                        blockManager.giveTycoonBlock(p, TycoonType.IRON);
                        return true;
                    case "diamond":
                        blockManager.giveTycoonBlock(p, TycoonType.DIAMOND);
                        return true;
                    default:
                        p.sendMessage(ChatColor.RED + "Not a valid tycoon type!");
                        return true;
                }
            case "menu":
                menuManager.openTycoonOverview(p, 0);
                break;
            case "open", "info", "stats":
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon open <index>");
                    return true;
                }
                int index;
                if (args[1] == null) {
                    p.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon open <index>");
                }
                try {
                    index = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage("§cIndex must be an integer!");
                    return true;
                }
                TycoonBlock tycoonBlock = blockManager.getTycoonBlockFromIndex(p, index);
                if (tycoonBlock == null) {
                    p.sendMessage(ChatColor.RED + "No tycoon block found!");
                    return true;
                }
                new StatsMenu(tycoonBlock, plugin).open(p);
        }
        return true;
    }


    public void handleToggle(Player player){
        if (!player.hasMetadata("viewing_tycoon")) {
            player.sendMessage(ChatColor.RED + "No Tycoon selected.");
            return;
        }

        String tycoonUID = player.getMetadata("viewing_tycoon").getFirst().asString();
        TycoonBlock tycoonBlock = blockManager.getTycoonBlock(tycoonUID);

        if (tycoonBlock == null) return;


        tycoonBlock.setActive(!tycoonBlock.isActive());

        // Soundeffekt für Feedback
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        if (tycoonBlock.isActive()) {
            player.sendMessage(ChatColor.GREEN + "Tycoon spawning...");
        }else{
            player.sendMessage(ChatColor.RED + "Tycoon spawning stopped.");
        }
    }
}





